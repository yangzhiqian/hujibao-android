package edu.ncu.safe.domain;

/**
 * Created by Mr_Yang on 2016/5/18.
 */
public class InterceptionSetInfo {
    private int mode = 0;
    private boolean isNightMode = false;
    private int beginHour = 23;
    private int beginMinute = 0;
    private int endHour = 6;
    private int endMinute = 0;
    private boolean[] weekEnable = {false, true, true, true, true, true, false};

    public InterceptionSetInfo() {
    }

    public InterceptionSetInfo(int mode,boolean isNightMode ,int beginHour, int beginMinute, int endHour, int endMinute, boolean[] weekEnable, int nightMode) {
        this.mode = mode;
        this.isNightMode = isNightMode;
        this.beginHour = beginHour;
        this.beginMinute = beginMinute;
        this.endHour = endHour;
        this.endMinute = endMinute;
        this.weekEnable = weekEnable;
        this.nightMode = nightMode;
    }

    public int getNightMode() {
        return nightMode;
    }

    public void setNightMode(int nightMode) {
        this.nightMode = nightMode;
    }

    private int nightMode = 2;

    public int getBeginHour() {
        return beginHour;
    }

    public void setBeginHour(int beginHour) {
        this.beginHour = beginHour;
    }

    public int getBeginMinute() {
        return beginMinute;
    }

    public void setBeginMinute(int beginMinute) {
        this.beginMinute = beginMinute;
    }

    public int getEndHour() {
        return endHour;
    }

    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    public int getEndMinute() {
        return endMinute;
    }

    public void setEndMinute(int endMinute) {
        this.endMinute = endMinute;
    }

    public boolean[] getWeekEnable() {
        return weekEnable;
    }

    public void setWeekEnable(boolean[] weekEnable) {
        this.weekEnable = weekEnable;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }
    public boolean isNightMode() {
        return isNightMode;
    }

    public void setIsNightMode(boolean isNightMode) {
        this.isNightMode = isNightMode;
    }


    public String getNote(){
        String week = "星期";
        if(weekEnable[1]){week+="一 ";}
        if(weekEnable[2]){week+="二 ";}
        if(weekEnable[3]){week+="三 ";}
        if(weekEnable[4]){week+="四 ";}
        if(weekEnable[5]){week+="五 ";}
        if(weekEnable[6]){week+="六 ";}
        if(weekEnable[0]){week+="天 ";}
        week=week.trim();
        week = week.replace(' ','、');
        if(week.equals("星期一、二、三、四、五、六、天")){
            week = "每天";
        }

        if(week.equals("星期一、二、三、四、五")){
            week="工作日";
        }

        String time = toTwoLength(beginHour)+":"+toTwoLength(beginMinute)+"-"+toTwoLength(endHour)+":"+toTwoLength(endMinute);
        String modeStr = "(模式"+(nightMode+1)+")";
        return week+" "+time+" "+modeStr;
    }
    public String toTwoLength(int i){
        String re = i+ "";
        if(re.length()!=2){
            re = "0"+re;
        }
        return re;
    }
}
