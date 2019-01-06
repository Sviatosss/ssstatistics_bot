package ssstatistics.com;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.toIntExact;

/**
 * Created by Sviat on 26.10.2018.
 */
public class Sending extends Ssstatistics_bot {

    public  void sendMsg(Update update, String s){
        if (update.hasCallbackQuery()){
            editMessage(update,s);
        }else {
            SendMessage sendMessage = new SendMessage();
            sendMessage.enableMarkdown(true);
            sendMessage.setChatId(update.getMessage().getChatId().toString());
            sendMessage.setText(s);
            sendMessage.disableWebPagePreview();
            try {
                execute(sendMessage);
            }catch (TelegramApiException e){
                e.printStackTrace();
            }
        }
    }
    public void editMessage(Update update, String answer){
        EditMessageText new_message = new EditMessageText()
                .setChatId(update.getCallbackQuery().getMessage().getChatId())
                .setMessageId(toIntExact(update.getCallbackQuery().getMessage().getMessageId()))
                .setText(answer);
        try {
            execute(new_message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    public void keyboards(Update update, ArrayList<String> comandList1, ArrayList<String> comandList2){
        SendMessage sendMessage = new SendMessage();

        sendMessage.setChatId(update.getMessage().getChatId().toString());
        sendMessage.enableMarkdown(true);

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboar = new ArrayList<>();


        KeyboardRow row = new KeyboardRow();
        for (String comand:  comandList1) {
            row.add(comand);
        }
	    KeyboardRow row2 = new KeyboardRow();
	    for (String comand:  comandList2) {
		    row2.add(comand);
	    }
        keyboar.add(row);
        keyboar.add(row2);
        replyKeyboardMarkup.setKeyboard(keyboar);
        replyKeyboardMarkup.setResizeKeyboard(true);
        sendMessage.setText("Вибиріть дію з меню ⬇️ ");
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        sendMessage.disableWebPagePreview();
        try {
            execute(sendMessage);
        }catch (TelegramApiException e){
            e.printStackTrace();
        }
    }
    public void sendingBootomNavs(Update update){
        ArrayList<String> arrayList = new ArrayList<>();
        ArrayList<String> arrayList2 = new ArrayList<>();
        arrayList.add("Мінус ➖");
        arrayList.add("Плюс ➕");
        arrayList2.add("\uD83D\uDCCA Статистика");
        keyboards(update, arrayList, arrayList2);
    }
    public void sendingPoints(Update update){
    	sendMsg(update, "Очків в загальному: " + UsersManager.getInstance().getPoints(update));
        ArrayList<String> arrayList = UsersManager.getInstance().getStory(update);
        for (String s: arrayList){
            if (s.contains(Functions.getInstance().getDete())){
                sendMsg(update, s);
            }
        }
    }
    public void sendingDailyPoints(Update update){
        String s = UsersManager.getInstance().getDailyPoints(update);
        sendMsg(update, "Очки: " + s.split(" ")[1] + "\n" + "Дата: " + s.split(" ")[0]);
    }
}
