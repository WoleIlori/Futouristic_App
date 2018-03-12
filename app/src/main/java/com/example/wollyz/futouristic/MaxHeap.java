package com.example.wollyz.futouristic;

import java.util.Arrays;

/**
 * Created by Wollyz on 01/02/2018.
 */
public class MaxHeap {
    private double[] heap;
    private int[] distPos;
    private int maxSize;
    private int size;
    private static final int ROOT = 1;
    private boolean notFull;
    private boolean reset;
    private int equalNodes; //used to check if subtrees have equal children

    MaxHeap(int maxSize)
    {
        this.maxSize = maxSize;
        size = 0;
        notFull = true;
        heap = new double[maxSize + 1];
        distPos = new int[maxSize+ 1];
        heap[0] = Double.MAX_VALUE;
        distPos[0] = Integer.MAX_VALUE;
        reset = false;
    }

    //parent index
    public int parent(int pos)
    {
        return pos / 2;
    }

    //left child index
    public int leftChild(int pos){
        return pos * 2;
    }

    //right child index
    public int rightChild(int pos){
        return( pos * 2) + 1;
    }

/*
    private boolean hasRightChild(int pos){
        if(leftChild(pos) == equalNodes){  //size
            return false;
        }
        else{
            return true;
        }
    }
*/




    //c_pos child pos, p_pos parent pos
    private void swap(int c_pos, int p_pos){
        double tmp;
        int tmpIndex;

        tmp = heap[c_pos];
        tmpIndex = distPos[c_pos];

        heap[c_pos] = heap[p_pos];
        distPos[c_pos] = distPos[p_pos];

        heap[p_pos] = tmp;
        distPos[p_pos] = tmpIndex;
    }

    public void insert(double dist, int distIndex){
        if(notFull) {
            size += 1;
            heap[size] = dist;
            distPos[size] = distIndex;
            heapifyUp(size);
            if (size == maxSize) {
                notFull = false;
            }


        }
        else
        {
            if(dist < heap[ROOT]){
                heap[ROOT] = dist;
                distPos[ROOT] = distIndex;
                buildMaxHeap();
            }
        }

    }

    public void EmptyHeap(){
        Arrays.fill(heap,0);
        heap[0] = Double.MAX_VALUE;


    }
    public void heapifyUp(int pos){
        int current = pos;
        while(heap[current] > heap[parent(current)]){
            swap(current, parent(current));
            current = parent(current);
        }

    }

    public void maxHeapify(int pos){
        int largest = pos;
        if(leftChild(pos) <= size && heap[pos] < heap[leftChild(pos)]){
            largest = leftChild(pos);
        }

        if(rightChild(pos) <= size && heap[pos] < heap[rightChild(pos)]){
            if(heap[largest] < heap[rightChild(pos)]){
                largest = rightChild(pos);
            }
        }


        if(largest != pos){
            swap(largest,pos);
            maxHeapify(largest);

        }


    }

    public void buildMaxHeap(){
        for(int pos =  size / 2; pos>=1; pos--){
            maxHeapify(pos);
        }
    }

    public int[] getHeap(){
        return distPos;
    }

    /*
    distPos[size] = distIndex;
          if (size == maxSize) {
              maxAlgorithm();
              full = true;
          }*/





}
