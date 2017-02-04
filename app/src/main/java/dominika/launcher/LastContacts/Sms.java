package dominika.launcher.LastContacts;

/**
 * Created by Domi on 01.02.2017.
 */

public class Sms {
    private String _address;
    private String _person;
    private String _msg;
    private String _time;

    public String getAddress(){
        return _address;
    }
    public String getMsg(){
        return _msg;
    }
    public String getTime(){
        return _time;
    }
    public String getPerson() {
        return _person;
    }

    public void setPerson(String _person) {
        this._person = _person;
    }
    public void setAddress(String address){
        _address = address;
    }
    public void setMsg(String msg){
        _msg = msg;
    }
    public void setTime(String time){
        _time = time;
    }

}