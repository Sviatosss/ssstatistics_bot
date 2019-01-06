package ssstatistics.com;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public class Ssstatistics_bot extends TelegramLongPollingBot {

    public void onUpdateReceived(Update update) {
        Sending sending = new Sending();
        if (update.hasMessage() && update.getMessage().hasText()) {
            String s = update.getMessage().getText();
            if (s.equals("/start")){
                sending.sendingBootomNavs(update);
                UsersManager.getInstance().issetUser(update);
            }else if (s.equals("Мінус ➖") || s.equals("Плюс ➕")){
                UsersManager.getInstance().addStory(update);
                if (s.equals("Плюс ➕")){
                    UsersManager.getInstance().addPoint(update);
                }else if (s.equals("Мінус ➖")){
                    UsersManager.getInstance().subtractPoint(update);
                }
                sending.sendingDailyPoints(update);
            }
            else if (s.equals("\uD83D\uDCCA Статистика")){
                sending.sendingPoints(update);
            }
            else {
                UsersManager.getInstance().updateDescription(update);
                sending.sendMsg(update, "Опис додано !");
            }
        }if (update.hasCallbackQuery()){
        }
    }
    public String getBotUsername() {
        return "ssstatistics_bot";
    }
    public String getBotToken() {
        return "733589645:AAH9RWGXCFhQrYfEEYd-sccOrQ2uwdNBWYQ";
    }
}