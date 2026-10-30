package com.chiefminingdad.autoplayer.graphics;

import com.chiefminingdad.autoplayer.AutoPlayer;
import com.chiefminingdad.autoplayer.AutoPlayerClient;
import com.chiefminingdad.autoplayer.node.Node;
import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelExtractionContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelExtractionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.StagedVertexBuffer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.Optional;
import java.util.OptionalDouble;

public class GraphicsManager implements ClientModInitializer {

    private static final RenderPipeline CONNECTED_LINE = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.LINES_SNIPPET)
            .withLocation(AutoPlayer.id("pipeline/connected_line"))
            .build()
    );

    private Line[] Lines;

    private static final Vector4f COLOR_MODULATOR = new Vector4f(1f, 1f, 1f, 1f);
    private static final Vector3f MODEL_OFFSET = new Vector3f();
    private static final Matrix4f TEXTURE_MATRIX = new Matrix4f();
    private static final StagedVertexBuffer stagedBuffer = new StagedVertexBuffer(() -> "Lines Buffer", RenderType.SMALL_BUFFER_SIZE);


    @Override
    public void onInitializeClient() {
        LevelExtractionEvents.END_EXTRACTION.register(this::ExtractLineLocations);
        LevelRenderEvents.AFTER_TRANSLUCENT_TERRAIN.register(this::renderAndDrawLines);
    }

    /**
     * Should extract the finished Path and store it for use here.
     * @param context Data that is not used right now.
     */
    private void ExtractLineLocations(LevelExtractionContext context){
        Node[] Path = AutoPlayerClient.pathManager.Path;
        Lines = new Line[Path.length-1];
        for (int i = 1; i < Path.length; i++) {
            Node n = Path[i];
            Lines[i] = new Line(n.previousLocation.getX()+0.5,n.previousLocation.getY(),n.previousLocation.getZ()+0.5,
                    n.location.getX()+0.5,n.location.getY(),n.location.getZ()+0.5
            );
        }
    }

    private void renderAndDrawLines(LevelRenderContext context){
        RenderPipeline renderPipeline = GraphicsManager.CONNECTED_LINE;
        VertexFormat vertexFormat = renderPipeline.getVertexFormatBinding(0);

        assert vertexFormat != null;

        PrimitiveTopology primitiveTopology = renderPipeline.getPrimitiveTopology();
        StagedVertexBuffer.Draw draw = stagedBuffer.appendDraw(vertexFormat, primitiveTopology, primitiveTopology == PrimitiveTopology.LINES ? RenderSystem.getProjectionType().vertexSorting() : null);

        this.renderLines(context,draw);

        stagedBuffer.upload();

        StagedVertexBuffer.ExecuteInfo info = stagedBuffer.getExecuteInfo(draw);

        if(info!=null){
            draw(Minecraft.getInstance(),info, renderPipeline);
        }

        stagedBuffer.endFrame();
    }

    private void renderLines(LevelRenderContext levelRenderContext, StagedVertexBuffer.Draw draw){
        PoseStack matrices = levelRenderContext.poseStack();
        Vec3 camera = levelRenderContext.levelState().cameraRenderState.pos;

        matrices.pushPose();
        matrices.translate(-camera.x,-camera.y,-camera.z);

        final VertexConsumer builder = stagedBuffer.getVertexBuilder(draw);

        for(Line line: Lines) {
            this.renderLine(matrices.last().pose(), builder,line);
            matrices.popPose();
        }
    }

    private void renderLine(Matrix4fc positionMatrix, VertexConsumer buffer, Line line){
        buffer.addVertex(positionMatrix, (float) line.startX, (float) line.startY, (float) line.startZ).setColor(0f,1f,0f,0.5f);
        buffer.addVertex(positionMatrix, (float) line.endX, (float) line.endY, (float) line.endZ).setColor(0f,1f,0f,0.5f);
    }

    public static void draw(Minecraft client, StagedVertexBuffer.ExecuteInfo info, RenderPipeline renderPipeline){
        GpuBufferSlice dynamicTransforms = RenderSystem.getDynamicUniforms()
                .writeTransform(RenderSystem.getModelViewMatrixCopy(), COLOR_MODULATOR, MODEL_OFFSET, TEXTURE_MATRIX);

        RenderTarget mainTarget = client.gameRenderer.mainRenderTarget();
        GpuTextureView colorTexture = mainTarget.getColorTextureView();

        assert colorTexture != null;

        try (RenderPass renderPass = RenderSystem.getDevice()
                .createCommandEncoder()
                .createRenderPass(()-> AutoPlayer.MOD_ID + " path line rendering.", colorTexture, Optional.empty(), mainTarget.getDepthTextureView(), OptionalDouble.empty() )){
            renderPass.setPipeline(renderPipeline);

            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.setUniform("DynamicTransforms", dynamicTransforms);

            renderPass.setVertexBuffer(0, info.vertexBuffer().slice());
            renderPass.setIndexBuffer(info.indexBuffer(), info.indexType());

            renderPass.drawIndexed(info.indexCount(), 1, info.firstIndex(), info.baseVertex(), 0);
        }
    }

    public static void close() {
        stagedBuffer.close();
    }

    private record Line(double startX, double startY, double startZ, double endX, double endY, double endZ){}
}
