package com.chiefminingdad.autoplayer.graphics;

import com.chiefminingdad.autoplayer.AutoPlayer;
import com.chiefminingdad.autoplayer.AutoPlayerClient;
import com.chiefminingdad.autoplayer.node.Node;
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import com.mojang.renderpearl.api.commands.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.level.*;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.StagedVertexBuffer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.Optional;

public class GraphicsManager implements ClientModInitializer {

    private static GraphicsManager instance;

    private static final RenderPipeline LINE_THROUGH_WALLS = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.LINES_SNIPPET)
            .withLocation(AutoPlayer.id("pipeline/line_through_walls"))
            .withDepthStencilState(Optional.empty())
            .build()
    );

    private Line[] Lines;

    private static final Vector4f COLOR_MODULATOR = new Vector4f(1f, 1f, 1f, 1f);
    private static final Vector3f MODEL_OFFSET = new Vector3f();
    private static final Matrix4f TEXTURE_MATRIX = new Matrix4f();
    private static final StagedVertexBuffer stagedBuffer = new StagedVertexBuffer(() -> "Lines Buffer", RenderType.SMALL_BUFFER_SIZE);
    private StagedVertexBuffer.Draw draw;

    public static GraphicsManager getInstance() {
        return instance;
    }

    @Override
    public void onInitializeClient() {
        instance = this;
        AutoPlayer.LOGGER.info("autoplayer Graphics Initialising");
        LevelExtractionEvents.END_EXTRACTION.register(this::ExtractLineLocations);
        LevelRenderEvents.BEFORE_RENDER_PASS.register(this::prepare);
        LevelRenderEvents.END_MAIN.register(_ -> stagedBuffer.endFrame());
//        LevelRenderEvents.AFTER_TRANSLUCENT_TERRAIN.register(this::renderAndDrawLines);
    }

    /**
     * Should extract the finished Path and store it for use here.
     * @param context Data that is not used right now.
     */
    private void ExtractLineLocations(LevelExtractionContext context){
        if (AutoPlayerClient.pathManager == null) return;
        Node[] Path = AutoPlayerClient.pathManager.Path;
        if (Path == null||Path.length==0) return;
        Lines = new Line[Path.length-1];
        for (int i = 1; i < Path.length; i++) {
            Node n = Path[i];
            Lines[i-1] = new Line(n.previousLocation.getX()+0.5,n.previousLocation.getY(),n.previousLocation.getZ()+0.5,
                    n.location.getX()+0.5,n.location.getY(),n.location.getZ()+0.5
            );
        }
    }

    private void prepare(LevelTerrainRenderContext context) {
        RenderPipeline renderPipeline = GraphicsManager.LINE_THROUGH_WALLS;
        VertexFormat formatBinding = renderPipeline.getVertexFormatBinding(0);

        assert formatBinding != null;

        PrimitiveTopology primitive = renderPipeline.getPrimitiveTopology();
        StagedVertexBuffer.Draw draw = stagedBuffer.appendDraw(formatBinding, primitive, primitive == PrimitiveTopology.DEBUG_LINES ? RenderSystem.getProjectionType().vertexSorting() : null);

        this.renderLines(context, draw);

        stagedBuffer.upload();

        this.draw = draw;
    }

    private void renderLines(LevelTerrainRenderContext levelRenderContext, StagedVertexBuffer.Draw draw){
        Matrix4f matrix = new Matrix4f();
        Vec3 camera = levelRenderContext.levelState().cameraRenderState.pos;

        matrix.translate((float) -camera.x, (float) -camera.y, (float) -camera.z);

        final VertexConsumer builder = stagedBuffer.getVertexBuilder(draw);
        if(Lines==null) return;
        int i = 0;
        for(Line line: Lines) {
            AutoPlayer.LOGGER.info(String.valueOf(i++));
            this.renderLine(matrix, builder,line);
        }
    }

    private void renderLine(Matrix4fc positionMatrix, VertexConsumer buffer, Line line){
        buffer.addVertex(positionMatrix, (float) line.startX, (float) line.startY, (float) line.startZ).setColor(1f,0f,0f,0.5f).setNormal(0,1,0).setLineWidth(1f);
        buffer.addVertex(positionMatrix, (float) line.endX, (float) line.endY, (float) line.endZ).setColor(1f,0f,0f,0.5f).setNormal(0,1,0).setLineWidth(1f);
    }

    public void draw(RenderPass renderPass){
        if (this.draw == null) {
            return;
        }

        StagedVertexBuffer.ExecuteInfo info = stagedBuffer.getExecuteInfo(this.draw);

        if (info == null) {
            return;
        }

        GpuBufferSlice dynamicTransforms = RenderSystem.getDynamicUniforms()
                .writeTransform(RenderSystem.getModelViewMatrixCopy(), COLOR_MODULATOR, MODEL_OFFSET, TEXTURE_MATRIX);

        renderPass.pushDebugGroup(() -> "Fabric Docs Example Pipeline Rendering");
        renderPass.setPipeline(RenderSystem.getCompiledPipeline(LINE_THROUGH_WALLS));

        RenderSystem.bindDefaultUniforms(renderPass);
        renderPass.setUniform("DynamicTransforms", dynamicTransforms);

        renderPass.setVertexBuffer(0, info.vertexBuffer().slice());
        renderPass.setIndexBuffer(info.indexBuffer(), info.indexType());

        renderPass.drawIndexed(info.indexCount(), 1, info.firstIndex(), info.baseVertex(), 0);
        renderPass.popDebugGroup();
    }

    public static void close() {
        stagedBuffer.close();
    }

    private record Line(double startX, double startY, double startZ, double endX, double endY, double endZ){}
}
