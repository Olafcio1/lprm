package pl.olafcio.lprm.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pl.olafcio.lprm.Main;

@Mixin(net.minecraft.client.MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Shadow
    public static MinecraftClient getInstance() {
        return null;
    }

    @Inject(at = @At("TAIL"), method = {"<init>"})
    public void init(RunArgs args, CallbackInfo ci) {
        Main.mc = getInstance();
    }
}
