package me.chunsheng.ebooks;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright © 2016 edaixi. All Rights Reserved.
 * Author: wei_spring
 * Date: 2016/11/14
 * Email:weichsh@edaixi.com
 * Function:
 */
public class Tree<T> {
    private Node<T> root;

    public Tree(T rootData) {
        root = new Node<T>();
        root.data = rootData;
        root.children = new ArrayList<Node<T>>();
    }

    public static class Node<T> {
        private T data;
        private Node<T> parent;
        private List<Node<T>> children;
    }

    public static void main(String[] args) {
        Log.e("Tag:","coem in java...");
    }
}