package whitewire.behappy;

/**
 * Created by Claudio on 26-Feb-17.
 */

public class Message {
    private int mMood;
    private int mDay, mMonth, mYear;
    private String mMessage;

    public Message(String message, int mood, int day, int month, int year) {
        mMessage = message;
        mMood = mood;
        mDay = day;
        mMonth = month;
        mYear = year;
    }

    public String getMessage() {
        return mMessage;
    }

    public String getDate() {
        return Integer.toString(mDay) + "/" + Integer.toString(mMonth) + "/" + Integer.toString(mYear);
    }

    @Override
    public String toString() {
        return mMessage;
    }

    public int getMood() {
        return mMood;
    }
}
