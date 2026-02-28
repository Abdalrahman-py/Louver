package com.example.louver.data.entity;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import com.example.louver.data.converter.FuelType;
import com.example.louver.data.converter.TransmissionType;
import static androidx.room.ForeignKey.RESTRICT;
@Entity(
        tableName = "cars",
        foreignKeys = {
                @ForeignKey(
                        entity = CategoryEntity.class,
                        parentColumns = "id",
                        childColumns = "categoryId",
                        onDelete = RESTRICT
                )
        },
        indices = {
                @Index(value = {"categoryId"})
        }
)
public class CarEntity implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    public long id;
    @ColumnInfo(name = "categoryId")
    public long categoryId;
    @ColumnInfo(name = "name")
    public String name;
    @ColumnInfo(name = "model")
    public String model;
    @ColumnInfo(name = "year")
    public int year;
    @ColumnInfo(name = "dailyPrice")
    public double dailyPrice;
    @ColumnInfo(name = "isAvailable")
    public boolean isAvailable;
    @ColumnInfo(name = "transmission")
    public TransmissionType transmission;
    @ColumnInfo(name = "fuelType")
    public FuelType fuelType;
    @ColumnInfo(name = "seats")
    public int seats;
    @Nullable
    @ColumnInfo(name = "fuelConsumption")
    public Double fuelConsumption;
    @Nullable
    @ColumnInfo(name = "description")
    public String description;
    @Nullable
    @ColumnInfo(name = "mainImageUrl")
    public String mainImageUrl;
    @ColumnInfo(name = "createdAt")
    public long createdAt;
    public CarEntity() {}
    public CarEntity(long categoryId, String name, String model, int year, double dailyPrice,
                     boolean isAvailable, TransmissionType transmission, FuelType fuelType,
                     int seats, @Nullable Double fuelConsumption, @Nullable String description,
                     @Nullable String mainImageUrl, long createdAt) {
        this.categoryId = categoryId;
        this.name = name;
        this.model = model;
        this.year = year;
        this.dailyPrice = dailyPrice;
        this.isAvailable = isAvailable;
        this.transmission = transmission;
        this.fuelType = fuelType;
        this.seats = seats;
        this.fuelConsumption = fuelConsumption;
        this.description = description;
        this.mainImageUrl = mainImageUrl;
        this.createdAt = createdAt;
    }
    // ── Parcelable ────────────────────────────────────────────────────────────
    protected CarEntity(Parcel in) {
        id          = in.readLong();
        categoryId  = in.readLong();
        name        = in.readString();
        model       = in.readString();
        year        = in.readInt();
        dailyPrice  = in.readDouble();
        isAvailable = in.readByte() != 0;
        String transName = in.readString();
        transmission = transName != null ? TransmissionType.valueOf(transName) : null;
        String fuelName  = in.readString();
        fuelType     = fuelName  != null ? FuelType.valueOf(fuelName)          : null;
        seats        = in.readInt();
        if (in.readByte() == 1) {
            fuelConsumption = in.readDouble();
        } else {
            fuelConsumption = null;
        }
        description  = in.readString();
        mainImageUrl = in.readString();
        createdAt    = in.readLong();
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(categoryId);
        dest.writeString(name);
        dest.writeString(model);
        dest.writeInt(year);
        dest.writeDouble(dailyPrice);
        dest.writeByte((byte) (isAvailable ? 1 : 0));
        dest.writeString(transmission != null ? transmission.name() : null);
        dest.writeString(fuelType     != null ? fuelType.name()     : null);
        dest.writeInt(seats);
        if (fuelConsumption != null) {
            dest.writeByte((byte) 1);
            dest.writeDouble(fuelConsumption);
        } else {
            dest.writeByte((byte) 0);
        }
        dest.writeString(description);
        dest.writeString(mainImageUrl);
        dest.writeLong(createdAt);
    }
    @Override
    public int describeContents() { return 0; }
    public static final Creator<CarEntity> CREATOR = new Creator<CarEntity>() {
        @Override
        public CarEntity createFromParcel(Parcel in) { return new CarEntity(in); }
        @Override
        public CarEntity[] newArray(int size) { return new CarEntity[size]; }
    };
}
