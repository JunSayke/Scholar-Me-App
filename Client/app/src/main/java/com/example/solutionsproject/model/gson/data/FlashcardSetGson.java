package com.example.solutionsproject.model.gson.data;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import lombok.Getter;

@Getter
public class FlashcardSetGson extends GsonData implements Parcelable {
    private int flashcardSetId;
    private int userid;
    private String title;
    private String description;
    private String dateAdded;
    private String dateUpdated;

    protected FlashcardSetGson(Parcel in) {
        flashcardSetId = in.readInt();
        userid = in.readInt();
        title = in.readString();
        description = in.readString();
        dateAdded = in.readString();
        dateUpdated = in.readString();
    }

    public static final Creator<FlashcardSetGson> CREATOR = new Creator<FlashcardSetGson>() {
        @Override
        public FlashcardSetGson createFromParcel(Parcel in) {
            return new FlashcardSetGson(in);
        }

        @Override
        public FlashcardSetGson[] newArray(int size) {
            return new FlashcardSetGson[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(flashcardSetId);
        dest.writeInt(userid);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(dateAdded);
        dest.writeString(dateUpdated);
    }
}
