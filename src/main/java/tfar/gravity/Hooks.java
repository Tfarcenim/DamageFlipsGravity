package tfar.gravity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import tfar.damageflipsgravity.mixin.CameraAccess;
import tfar.gravity.ducks.PlayerDuck;

public class Hooks {

    private static boolean playerRotationNeedToPop;
    private static boolean nameplateNeedToPop = false;

    public static final int FLIP_TIMER = 15;

    public static void onRenderPlayerPre(AbstractClientPlayer player, float yaw, float deltaTime, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight) {

        PlayerDuck duck = (PlayerDuck)player;

        double interpolatedPitch = (player.xRotO + (player.getXRot() - player.xRotO) * deltaTime);
        double interpolatedYaw = (player.yRotO + (player.getYRot() - player.yRotO) * deltaTime);

        interpolatedPitch %= 360;
        interpolatedYaw %= 360;


        if (duck.getGravity()) {
            poseStack.pushPose();
            if (duck.getTimer() > 0) {

            } else {
                ////flip upside down

                Vector3f vector3f = new Vector3f((float)interpolatedPitch,0,(float)interpolatedYaw);

                vector3f.normalize();

                poseStack.mulPose(vector3f.rotationDegrees(180));
                //poseStack.mulPose(Vector3f.ZP.rotation(180));
                poseStack.translate(0,-player.getBbHeight(),0);
            }
            playerRotationNeedToPop = true;
        } else if (duck.getTimer() > 0) {
            poseStack.pushPose();
            playerRotationNeedToPop = true;
        }
    }

    private static void cleanup(PoseStack stack) {
        if (playerRotationNeedToPop) {
            playerRotationNeedToPop = false;
            stack.popPose();
        }
    }

    public static void onCameraSetup(GameRenderer gameRenderer, Camera mainCamera, PoseStack poseStack, float deltaTime) {
        Minecraft minecraft = Minecraft.getInstance();
        Entity renderViewEntity = minecraft.getCameraEntity();
        if (renderViewEntity instanceof Player player) {
            PlayerDuck duck = (PlayerDuck) player;
            boolean gravityDirection = duck.getGravity();
            int timer = duck.getTimer();

            double interpolatedPitch = (player.xRotO + (player.getXRot() - player.xRotO) * deltaTime);
            double interpolatedYaw = (player.yRotO + (player.getYRot() - player.yRotO) * deltaTime);

            interpolatedPitch %= 360;
            interpolatedYaw %= 360;

            //todo: why does this jitter?
            float exactTime = timer - deltaTime;


            Vec3 lookVec = new Vec3(interpolatedPitch,0,interpolatedYaw).normalize();
            Vector3f vector3f = new Vector3f(lookVec);

            //poseStack.mulPose(vector3f.rotationDegrees(180));

            //flipping to up or on the way
            if (gravityDirection) {
                if (timer > 0) {
                    float degrees = 180 - (180f / FLIP_TIMER) * exactTime;
                    poseStack.mulPose(vector3f.rotationDegrees(degrees));
                    poseStack.mulPose(Vector3f.YP.rotationDegrees(degrees));
                } else {
                    poseStack.mulPose(Vector3f.XP.rotationDegrees(180));
                    poseStack.mulPose(Vector3f.YP.rotationDegrees(180));
                }
                //flipping from upside down to normal
            } else if (timer > 0) {
                float degrees = (180f / FLIP_TIMER) * exactTime;
                poseStack.mulPose(vector3f.rotationDegrees(degrees));
                poseStack.mulPose(Vector3f.YP.rotationDegrees(degrees));
            }
        }
    }

    public static void onRenderPlayerPost(AbstractClientPlayer abstractClientPlayer, float f, float g, PoseStack poseStack, MultiBufferSource multiBufferSource, int i) {
        cleanup(poseStack);
    }
}
