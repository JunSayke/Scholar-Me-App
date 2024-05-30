package com.example.solutionsproject.model.gson.data;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import lombok.Getter;

@Getter
public class FlashcardGson extends GsonData implements Parcelable {
    private int flashcardId;
    private int userid;
    private String question;
    private String dateAdded;
    private String dateUpdated;

    public FlashcardGson(int flashcardId) {
        this.flashcardId = flashcardId;
    }

    protected FlashcardGson(Parcel in) {
        flashcardId = in.readInt();
        userid = in.readInt();
        question = in.readString();
        dateAdded = in.readString();
        dateUpdated = in.readString();
    }

    public static final Creator<FlashcardGson> CREATOR = new Creator<FlashcardGson>() {
        @Override
        public FlashcardGson createFromParcel(Parcel in) {
            return new FlashcardGson(in);
        }

        @Override
        public FlashcardGson[] newArray(int size) {
            return new FlashcardGson[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(flashcardId);
        dest.writeInt(userid);
        dest.writeString(question);
        dest.writeString(dateAdded);
        dest.writeString(dateUpdated);
    }
}