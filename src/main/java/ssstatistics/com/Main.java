package ssstatistics.com;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Created by sviatosss on 06.01.2019.
 */
public class Main {
public static void main(String[] args){
	ApiContextInitializer.init();
	TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
	try {
		telegramBotsApi.registerBot(new Ssstatistics_bot());
	} catch (TelegramApiException e) {
		e.printStackTrace();
	}
}
}
