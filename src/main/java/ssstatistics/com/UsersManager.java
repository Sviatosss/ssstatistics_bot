package ssstatistics.com;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;

import static com.mongodb.client.model.Filters.eq;

/**
 * Created by sviatosss on 18.12.2018.
 */
public class UsersManager {
    private static UsersManager sInstance;
    public MongoCollection<Document>  mUsersCollection = DataBaseManager.getInstance().getmUsersCollection();

    public static UsersManager getInstance() {
        if (sInstance == null) {
            sInstance = new UsersManager();
        }

        return sInstance;
    }

    public void issetUser(Update update){
        String id = Functions.getInstance().getId(update);
        Document query = new Document("id", id);
        Document user = mUsersCollection.find(query).first();

        if (user == null) {
            ArrayList<String> arrayList = new ArrayList<>();
            arrayList.add("Ви долучились - " + Functions.getInstance().getDete());
            Document newUser = new Document("id", id)
                    .append("firstName", update.getMessage().getChat().getFirstName())
                    .append("lastName", update.getMessage().getChat().getLastName())
                    .append("username", update.getMessage().getChat().getUserName())
                    .append("points", 0)
                    .append("daily_points", Functions.getInstance().getDete() + " " + 0)
                    .append("statistics", new ArrayList<String>());
            mUsersCollection.insertOne(newUser);
            System.out.println("New user");
        }
    }
//  start of points functions
    public int getPoints(Update update){
        String id = Functions.getInstance().getId(update);
        Document query = new Document("id", id);
        Document user = mUsersCollection.find(query).first();
        return user.getInteger("points");
    }
    public void addPoint(Update update){
        String id = Functions.getInstance().getId(update);
        mUsersCollection.updateOne(eq("id", id), new Document("$set", new Document("points", (getPoints(update) + 1))));
        addDailyPoint(update);
    }
    public void subtractPoint(Update update){
        String id = Functions.getInstance().getId(update);
        mUsersCollection.updateOne(eq("id", id), new Document("$set", new Document("points", (getPoints(update) - 1))));
        subtractDailyPoint(update);
    }
    
    public void isCurrentDailyDate(Update update){
        if (getDailyPoints(update).split(" ")[0].equals(Functions.getInstance().getDete())) {
            return;
        }else {
            String id = Functions.getInstance().getId(update);
            mUsersCollection.updateOne(eq("id", id), new Document("$set", new Document("daily_points", Functions.getInstance().getDete() + " " + 0)));
        }
    }
    public String getDailyPoints(Update update){
        String id = Functions.getInstance().getId(update);
        Document query = new Document("id", id);
        Document user = mUsersCollection.find(query).first();
        return user.getString("daily_points");
    }
    public void addDailyPoint(Update update){
        String id = Functions.getInstance().getId(update);
        int currentDailyPoints = Integer.parseInt(getDailyPoints(update).split(" ")[1]);
        mUsersCollection.updateOne(eq("id", id), new Document("$set", new Document("daily_points",  Functions.getInstance().getDete() + " " + (++currentDailyPoints))));
    }
    public void subtractDailyPoint(Update update){
        String id = Functions.getInstance().getId(update);
        int currentDailyPoints = Integer.parseInt(getDailyPoints(update).split(" ")[1]);
        mUsersCollection.updateOne(eq("id", id), new Document("$set", new Document("daily_points", Functions.getInstance().getDete() + " " + (--currentDailyPoints))));
    }
//   end of points functions

//  start of statistics functions
public ArrayList<String> getStory(Update update){
        String id = Functions.getInstance().getId(update);
        Document query = new Document("id", id);
        Document user = mUsersCollection.find(query).first();
        return (ArrayList<String>) user.get("statistics");
    }
    public void addStory(Update update){
        isCurrentDailyDate(update);
        ArrayList<String> arrayList = UsersManager.getInstance().getStory(update);
        arrayList.add(update.getMessage().getText() +
                "\nДата: " + Functions.getInstance().getDete() +
                "\nЧас: " + Functions.getInstance().getTime() +
                "\nОпис: " + "немає");
        String id = Functions.getInstance().getId(update);
        mUsersCollection.updateOne(eq("id", id), new Document("$set", new Document("statistics", arrayList)));
    }
    public void updateDescription(Update update){
        ArrayList<String> arrayList = getStory(update);
        String oldString = arrayList.get(arrayList.size() - 1);
        arrayList.remove(arrayList.size() - 1);
        oldString = oldString.split("\n")[0] + "\n" +
                oldString.split("\n")[1] + "\n" +
                oldString.split("\n")[2] + "\n" +
                "Опис: " + update.getMessage().getText();
        arrayList.add(oldString);
        String id = Functions.getInstance().getId(update);
        mUsersCollection.updateOne(eq("id", id), new Document("$set", new Document("statistics", arrayList)));
    }
//   end of statistics functions
}
