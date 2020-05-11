package com.aks.covidtracker.models;

public class OverviewItem {

    private static final String LOG_TAG = "OverviewItem";
    private String state, confirmed, active, recovered, deceased;
    private String confirmed_today, active_today, recovered_today, deceased_today;

    public OverviewItem(String state, String confirmed, String active, String recovered, String deceased, String confirmed_today, String active_today, String recovered_today, String deceased_today) {
        this.state = state;
        this.confirmed = confirmed;
        this.active = active;
        this.recovered = recovered;
        this.deceased = deceased;
        this.confirmed_today = confirmed_today;
        this.active_today = active_today;
        this.recovered_today = recovered_today;
        this.deceased_today = deceased_today;
    }

//    public OverviewItem(String state, String confirmed, String active, String recovered, String deceased) {
//        this.state = state;
//        this.confirmed = confirmed;
//        this.active = active;
//        this.recovered = recovered;
//        this.deceased = deceased;
//    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getConfirmed_today() {
        return confirmed_today;
    }

    public void setConfirmed_today(String confirmed_today) {
        this.confirmed_today = confirmed_today;
    }

    public String getActive_today() {
        return active_today;
    }

    public void setActive_today(String active_today) {
        this.active_today = active_today;
    }

    public String getRecovered_today() {
        return recovered_today;
    }

    public void setRecovered_today(String recovered_today) {
        this.recovered_today = recovered_today;
    }

    public String getDeceased_today() {
        return deceased_today;
    }

    public void setDeceased_today(String deceased_today) {
        this.deceased_today = deceased_today;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getRecovered() {
        return recovered;
    }

    public void setRecovered(String recovered) {
        this.recovered = recovered;
    }

    public String getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(String confirmed) {
        this.confirmed = confirmed;
    }

    public String getDeceased() {
        return deceased;
    }

    public void setDeceased(String deceased) {
        this.deceased = deceased;
    }

}
