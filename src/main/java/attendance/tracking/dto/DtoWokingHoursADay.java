package attendance.tracking.dto;

/**
 * Created by Andriy on 03/14/2016.
 */
public class DtoWokingHoursADay {
    private String date;
    private int workingHours;

    public DtoWokingHoursADay(String date, int workingHours) {
        this.date = date;
        this.workingHours = workingHours;
    }

    public DtoWokingHoursADay() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getWorkingHours() {
        return workingHours;
    }

    public void setWorkingHours(int workingHours) {
        this.workingHours = workingHours;
    }
}
