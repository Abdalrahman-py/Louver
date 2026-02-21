package com.example.louver.ui.admin;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.louver.data.converter.FuelType;
import com.example.louver.data.converter.TransmissionType;
import com.example.louver.data.entity.CarEntity;
import com.example.louver.data.repository.BookingRepository;
import com.example.louver.data.repository.CarRepository;

import java.util.List;

public class AdminCarsViewModel extends ViewModel {

    private final CarRepository carRepository;
    private final BookingRepository bookingRepository;
    private final LiveData<List<CarEntity>> cars;

    private final MutableLiveData<String> messageLD = new MutableLiveData<>();
    private final MutableLiveData<CarEntity> editingCarLD = new MutableLiveData<>();
    private final MutableLiveData<Boolean> saveSuccessLD = new MutableLiveData<>();

    public AdminCarsViewModel(@NonNull CarRepository carRepository, @NonNull BookingRepository bookingRepository) {
        this.carRepository = carRepository;
        this.bookingRepository = bookingRepository;
        this.cars = carRepository.getAllCars();
    }

    public LiveData<List<CarEntity>> getCars() { return cars; }
    public LiveData<String> getMessage() { return messageLD; }
    public LiveData<CarEntity> getEditingCar() { return editingCarLD; }
    public LiveData<Boolean> getSaveSuccess() { return saveSuccessLD; }

    public void startEdit(CarEntity carEntity) { editingCarLD.setValue(carEntity); }
    public void resetEditing() { editingCarLD.setValue(null); }
    public void clearSaveSuccess() { saveSuccessLD.setValue(null); }

    public void saveCar(
            CarEntity existing,
            String categoryIdInput,
            String name,
            String model,
            String yearInput,
            String dailyPriceInput,
            boolean isAvailable,
            String transmissionInput,
            String fuelTypeInput,
            String seatsInput,
            String fuelConsumptionInput,
            String description,
            String imageUrl
    ) {
        String validationError = validate(categoryIdInput, name, model, yearInput, dailyPriceInput, seatsInput, transmissionInput, fuelTypeInput);
        if (validationError != null) {
            messageLD.setValue(validationError);
            return;
        }

        CarEntity car = existing != null ? existing : new CarEntity();

        car.categoryId = Long.parseLong(categoryIdInput.trim());
        car.name = name.trim();
        car.model = model.trim();
        car.year = Integer.parseInt(yearInput.trim());
        car.dailyPrice = Double.parseDouble(dailyPriceInput.trim());
        car.isAvailable = isAvailable;
        car.transmission = TransmissionType.valueOf(transmissionInput);
        car.fuelType = FuelType.valueOf(fuelTypeInput);
        car.seats = Integer.parseInt(seatsInput.trim());
        car.fuelConsumption = fuelConsumptionInput.trim().isEmpty() ? null : Double.parseDouble(fuelConsumptionInput.trim());
        car.description = trimToNull(description);
        car.mainImageUrl = trimToNull(imageUrl);

        if (existing == null) {
            car.createdAt = System.currentTimeMillis();
            carRepository.insert(car);
            messageLD.setValue("Car created successfully");
        } else {
            carRepository.update(car);
            messageLD.setValue("Car updated successfully");
        }

        resetEditing();
        saveSuccessLD.setValue(true);
    }

    public void deleteCar(CarEntity carEntity) {
        bookingRepository.hasBookingsForCar(carEntity.id, hasBookings -> {
            if (hasBookings) {
                messageLD.postValue("Cannot delete this car because it has bookings");
                return;
            }
            carRepository.delete(carEntity);
            messageLD.postValue("Car deleted successfully");
            resetEditing();
        });
    }

    private String validate(
            String categoryId,
            String name,
            String model,
            String year,
            String dailyPrice,
            String seats,
            String transmission,
            String fuelType
    ) {
        if (categoryId == null || categoryId.trim().isEmpty()) return "Category ID is required";
        if (name == null || name.trim().isEmpty()) return "Name is required";
        if (model == null || model.trim().isEmpty()) return "Model is required";
        if (year == null || year.trim().isEmpty()) return "Year is required";
        if (dailyPrice == null || dailyPrice.trim().isEmpty()) return "Daily price is required";
        if (seats == null || seats.trim().isEmpty()) return "Seats is required";
        if (transmission == null || transmission.trim().isEmpty()) return "Transmission is required";
        if (fuelType == null || fuelType.trim().isEmpty()) return "Fuel type is required";
        try {
            if (Integer.parseInt(year.trim()) < 1950) return "Year is invalid";
            if (Double.parseDouble(dailyPrice.trim()) <= 0) return "Daily price must be greater than 0";
            if (Integer.parseInt(seats.trim()) <= 0) return "Seats must be greater than 0";
            Long.parseLong(categoryId.trim());
            TransmissionType.valueOf(transmission.trim());
            FuelType.valueOf(fuelType.trim());
        } catch (IllegalArgumentException e) {
            return "Please provide valid values";
        }
        return null;
    }

    private String trimToNull(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
