package com.aks.covidtracker.models;

public class OverviewItem {

    private static final String LOG_TAG = "OverviewItem";
    private String state, confirmed, active, recovered, deceased;
    private String confirmed_today, active_today, recovered_today, deceased_today;
    private String recovery_rate, mortality_rate, new_cases_rate;
    private String new_cases_2w, recovered_2w, death_2w;
    private String recovery_rate_2w, mortality_rate_2w, new_cases_rate_2w;

    public String getRecovery_rate_2w() {
        return recovery_rate_2w;
    }

    public void setRecovery_rate_2w(String recovery_rate_2w) {
        this.recovery_rate_2w = recovery_rate_2w;
    }

    public String getMortality_rate_2w() {
        return mortality_rate_2w;
    }

    public void setMortality_rate_2w(String mortality_rate_2w) {
        this.mortality_rate_2w = mortality_rate_2w;
    }

    public String getNew_cases_rate_2w() {
        return new_cases_rate_2w;
    }

    public void setNew_cases_rate_2w(String new_cases_rate_2w) {
        this.new_cases_rate_2w = new_cases_rate_2w;
    }

    public String getRecovery_rate() {
        return recovery_rate;
    }

    public void setRecovery_rate(String recovery_rate) {
        this.recovery_rate = recovery_rate;
    }

    public String getNew_cases_rate() {
        return new_cases_rate;
    }

    public void setNew_cases_rate(String new_cases_rate) {
        this.new_cases_rate = new_cases_rate;
    }

    public String getMortality_rate() {
        return mortality_rate;
    }

    public void setMortality_rate(String mortality_rate) {
        this.mortality_rate = mortality_rate;
    }


    public String getNew_cases_2w() {
        return new_cases_2w;
    }

    public void setNew_cases_2w(String new_cases_2w) {
        this.new_cases_2w = new_cases_2w;
    }

    public String getRecovered_2w() {
        return recovered_2w;
    }

    public void setRecovered_2w(String recovered_2w) {
        this.recovered_2w = recovered_2w;
    }

    public String getDeath_2w() {
        return death_2w;
    }

    public void setDeath_2w(String death_2w) {
        this.death_2w = death_2w;
    }

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
