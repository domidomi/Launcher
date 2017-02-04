package dominika.launcher.LastContacts;

/**
 * Created by Domi on 03.02.2017.
 */

public class Call {
    private String number;
    private String name;
    private String duration;
    private String date;

    public String getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    public String getDuration() {
        return duration;
    }

    public String getDate() {
        return date;
    }

    public void setName(String name) {
        if (name == null) {
            this.name = this.number;
        } else {
            this.name = name;
        }
    }
    public void setNumber(String number){
        this.number = number;
    }
    public void setDuration(String duration){
        this.duration = duration;
    }
    public void setDate(String date){
        this.date = date;
    }

}