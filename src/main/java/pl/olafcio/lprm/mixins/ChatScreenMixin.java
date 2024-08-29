package pl.olafcio.lprm.mixins;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pl.olafcio.lprm.GradientUtil;
import pl.olafcio.lprm.Main;
import pl.olafcio.lprm.MyUtils;

@Mixin(net.minecraft.client.gui.screen.ChatScreen.class)
public class ChatScreenMixin {
    @Inject(at = @At("HEAD"), method = "sendMessage", cancellable = true)
    public void sendMessage(String chatText, boolean addToHistory, CallbackInfo ci) {
        if (chatText.startsWith(":")) {
            ci.cancel();
            Main.mc.inGameHud.getChatHud().addToMessageHistory(chatText);

            ArrayList<String> args = new ArrayList<>(Arrays.stream(chatText.substring(1).split(" ")).toList());
            String cmd = args.removeFirst();

            if (Objects.equals(cmd, "create")) {
                if (args.size() < 3) {
                    Main.mc.player.sendMessage(Text.of("§6[LPRM]§7 Usage: §lcreate [ rank name ] [gradient color1 hex] [gradient color2 hex]"));
                    return;
                }

                String gradc1 = args.remove(args.size() - 2);
                String gradc2 = args.removeLast();
                String text = String.join(" ", args);
                String id = text.toLowerCase().replace(" ", "-");
                text = MyUtils.smallCaps(text);
                String gradient = GradientUtil.generateMessage(gradc1, gradc2, text);
                gradient = gradient.replace("\"", "\\\"");
                String finalGradient = gradient;
                (new Thread(() -> {
                    try {
                        Main.mc.player.networkHandler.sendCommand("lp creategroup " + id);
                        Thread.sleep(600L);
                        Main.mc.player.networkHandler.sendCommand("lp group " + id + " setdisplayname \"" + finalGradient + "\"");
                        Thread.sleep(600L);
                        Main.mc.player.networkHandler.sendCommand("lp group " + id + " meta setprefix 90 \"" + finalGradient + "&7 »&r \"");
                        Thread.sleep(500L);
                        Main.mc.player.sendMessage(Text.of("§6[LPRM]§7 Rank created. It has been given the ID: §l" + id));
                    } catch (InterruptedException var3) {
                        InterruptedException e = var3;
                        throw new RuntimeException(e);
                    }
                })).start();
            } else if (Objects.equals(cmd, "grant")) {
                if (args.size() != 2) {
                    Main.mc.player.sendMessage(Text.of("§6[LPRM]§7 Usage: §lgrant [player name] [rank id]"));
                    return;
                }

                var var10000 = Main.mc.player.networkHandler;
                String var10001 = args.get(0);
                var10000.sendCommand("lp user " + var10001 + " parent set " + args.get(1));
                Main.mc.player.sendMessage(Text.of("Rank granted."));
            } else {
                Main.mc.player.sendMessage(Text.of("§6[LPRM]§7 Unknown command. To see all commands, run §l:help§7."));
            }
        }
    }
}
