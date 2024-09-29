package pl.olafcio.lprm.mixins;

import java.util.ArrayList;
import java.util.Arrays;
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

            switch (cmd) {
                case "idcreate" -> {
                    if (args.size() < 4) {
                        Main.mc.player.sendMessage(Text.of("§6[LPRM]§7 Usage: §lidcreate [rank ID] {format codes + rank name} [gradient color1 hex] [gradient color2 hex]"));
                        return;
                    }

                    String gradc2 = args.removeLast(),
                            gradc1 = args.removeLast(),
                            id = args.removeFirst();
                    var text = String.join(" ", args);
                    text = MyUtils.smallCaps(text);
                    var formatCodes = "";
                    var inColor = false;
                    for (var ch : text.split("")) {
                        if (inColor)
                            formatCodes += "&" + ch;
                        else if (ch == "&")
                            inColor = true;
                        else break;
                    }
                    String gradient = GradientUtil.generateMessage(gradc1, gradc2, text, formatCodes);
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
                }
                case "create" -> {
                    if (args.size() < 3) {
                        Main.mc.player.sendMessage(Text.of("§6[LPRM]§7 Usage: §lcreate {format codes + rank name} [gradient color1 hex] [gradient color2 hex] [format codes]"));
                        return;
                    }

                    String gradc2 = args.removeLast(),
                           gradc1 = args.removeLast();
                    var text = String.join(" ", args);
                    var id = text.replace(" ", "-").replace("_", "-").replace(" ", "-");
                    text = MyUtils.smallCaps(text);
                    var formatCodes = "";
                    var inColor = false;
                    for (var ch : text.split("")) {
                        if (inColor)
                            formatCodes += "&" + ch;
                        else if (ch == "&")
                            inColor = true;
                        else break;
                    }
                    String gradient = GradientUtil.generateMessage(gradc1, gradc2, text, formatCodes);
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
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    })).start();
                }
                case "grant" -> {
                    if (args.size() != 2) {
                        Main.mc.player.sendMessage(Text.of("§6[LPRM]§7 Usage: §lgrant [player name] [rank id]"));
                        return;
                    }

                    var plr = args.get(0);
                    Main.mc.player.networkHandler.sendCommand("lp user " + plr + " parent set " + args.get(1));
                    Main.mc.player.sendMessage(Text.of("§6[LPRM]§7 Rank granted."));
                }
                case "help" -> {
                    Main.mc.player.sendMessage(Text.of("§6[LPRM]§7 Available commands:\n§6[LPRM]§7 :create - used to create a rank\n§6[LPRM]§7 :idcreate - used to create a rank with a specific ID\n§6[LPRM]§7 :grant - used to grant a rank to player"));
                }
                case null, default ->
                        Main.mc.player.sendMessage(Text.of("§6[LPRM]§7 Unknown command. To see all commands, run §l:help§7."));
            }
        }
    }
}
