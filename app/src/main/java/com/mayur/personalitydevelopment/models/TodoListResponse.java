package com.mayur.personalitydevelopment.models;

import java.io.Serializable;
import java.util.ArrayList;

public class TodoListResponse implements Serializable {

    private int status_code;

    private String message;

    private Data data;

    public int getStatus_code() {
        return this.status_code;
    }

    public void setStatus_code(int status_code) {
        this.status_code = status_code;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Data getData() {
        return this.data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data {
        private ArrayList<Cards> cards;

        public ArrayList<Cards> getCards() {
            return this.cards;
        }

        public void setCards(ArrayList<Cards> cards) {
            this.cards = cards;
        }
    }

    public class Cards implements Serializable {
        private int id;

        private String name;

        private ArrayList<Notes> notes;

        public int getId() {
            return this.id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public ArrayList<Notes> getNotes() {
            return this.notes;
        }

        public void setNotes(ArrayList<Notes> notes) {
            this.notes = notes;
        }
    }

    public class Notes implements Serializable {
        private int id;

        private String title;

        private int card_id;

        private boolean is_checked;

        public int getId() {
            return this.id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return this.title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getCard_id() {
            return this.card_id;
        }

        public void setCard_id(int card_id) {
            this.card_id = card_id;
        }

        public boolean getIs_checked() {
            return this.is_checked;
        }

        public void setIs_checked(boolean is_checked) {
            this.is_checked = is_checked;
        }
    }
}



