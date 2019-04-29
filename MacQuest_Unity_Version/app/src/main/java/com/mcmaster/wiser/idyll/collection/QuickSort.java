package com.mcmaster.wiser.idyll.collection;

public class QuickSort {

    public int nCount_first20;
    public int nCount_first50;

    float readingArray[];



    public QuickSort(){
        nCount_first20 = 0;
        nCount_first50 = 0;
        readingArray = new float[50];

    }



    public boolean saveValue(float fValue){
        nCount_first20++;

        if(nCount_first20 <= 20 ){
            return false;
        }

        if(nCount_first50 >= 50){
            quickSort(readingArray);
            return true;
        }else
        {
            readingArray[nCount_first50] = fValue;
            nCount_first50++;
            return false;
        }

    }

    public float[] getReadingArray() {
        return readingArray;
    }

    public static void quickSort(float[] arr){
        qsort(arr, 0, arr.length-1);
    }
    private static void qsort(float[] arr, int low, int high){
        if (low < high){
            int pivot=partition(arr, low, high);
            qsort(arr, low, pivot-1);
            qsort(arr, pivot+1, high);
        }
    }
    private static int partition(float[] arr, int low, int high){
        float pivot = arr[low];
        while (low<high){
            while (low<high && arr[high]>=pivot) --high;
            arr[low]=arr[high];
            while (low<high && arr[low]<=pivot) ++low;
            arr[high] = arr[low];
        }

        arr[low] = pivot;

        return low;
    }
}
