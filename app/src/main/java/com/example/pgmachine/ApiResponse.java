package com.example.pgmachine;

import java.util.List;

public class ApiResponse {
    private int code;
    private String msg;
    private Data data;
    private long t;

    // Getters and setters
    public int getCode() {
        return code;
    }

    public Data getData() {
        return data;
    }

    public long getT() {
        return t;
    }

    public String getMsg() {
        return msg;
    }

    public static class Data {
        private List<Item> items;
        private int goodsPage;

        // Getters and setters
        public List<Item> getItems() {
            return items;
        }

        public int getGoodsPage() {
            return goodsPage;
        }

        public static class Item {
            private String id;
            private int type;
            private String name;
            private int status;

            // Getters and setters
            public String getId() {
                return id;
            }
            public int getType() {
                return type;
            }
            public String getName() {
                return name;
            }
            public int getStatus() {
                return status;
            }
        }
    }
}
