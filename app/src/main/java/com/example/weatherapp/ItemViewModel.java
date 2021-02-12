package com.example.weatherapp;

import androidx.lifecycle.ViewModel;

public class ItemViewModel extends ViewModel {
    private double[] selectedItem;
    public void selectItem(double item[]) {
        selectedItem = item;
    }
    public double[] getSelectedItem() {
        return selectedItem;
    }
}
