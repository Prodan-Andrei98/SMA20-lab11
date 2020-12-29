package com.example.lab7;

@IgnoreExtraProperties
public class MonthlyExpense {

    public String month;
    private float expenses, income;

    public MonthlyExpense() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public MonthlyExpense(String month, float expenses, float income) {
        this.month = month;
        this.income = income;
        this.expenses = expenses;
    }

    public String getMonth() {
        return month;
    }

    public float getExpenses() {
        return expenses;
    }

    public float getIncome() {
        return income;
    }
}