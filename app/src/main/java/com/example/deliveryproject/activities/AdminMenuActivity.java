package com.example.deliveryproject.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.deliveryproject.R;
import com.example.deliveryproject.fragments.AdminProfileFragment;
import com.example.deliveryproject.fragments.AdminRestaurantsFragment;
import com.example.deliveryproject.fragments.AdminShopsFragment;
import com.example.deliveryproject.fragments.AdminStatistics;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AdminMenuActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_menu);

        // По дефолту открываются магазины
        openShops();
        
        // Настройка нижней панели навигации
        BottomNavigationView bottomNavigationMenuView = findViewById(R.id.a_admin_bottom_menu);
        bottomNavigationMenuView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.admin_bottom_shops) {
                openShops();
            } else if (item.getItemId() == R.id.admin_bottom_restaurants) {
                openRests();
            } else if (item.getItemId() == R.id.admin_bottom_account) {
                openProfile();
            } else if (item.getItemId() == R.id.admin_bottom_statistics) {
                openStatistics();
            }
            return true;
        });
    }

    // Ссылка на базу данных
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    private void openStatistics() {
        // Ссылка на пользователей
        DatabaseReference usersRef = databaseReference.child("Users");

        // Получаем всех пользователей и "прослушиваем" результат
        usersRef.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                // Соединиться с базой не удалось
                Toast.makeText(this, "Плохое соединение с базой", Toast.LENGTH_SHORT).show();
                return;
            }

            List<DataSnapshot> arr = new ArrayList<>();

            // Массив пользователей
            Iterable<DataSnapshot> items = task.getResult().getChildren();

            // Конвертация пользователей из Iterable<DataSnapshot> в List<DataSnapshot>
            for (DataSnapshot dataSnapshot : items) {
                arr.add(dataSnapshot);
            }

            // Запускаем фрагмент статистики
            replaceFragment(new AdminStatistics(
                            arr,
                            getSupportFragmentManager()),
                    getSupportFragmentManager());
        });
    }

    public void openShops() {
        // Ссылка на магазины
        DatabaseReference shopsRef = databaseReference.child("Shops");

        // Получаем все магазины и "прослушиваем" результат
        shopsRef.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                // Соединиться с базой не удалось
                Toast.makeText(this, "Плохое соединение с базой", Toast.LENGTH_SHORT).show();
                return;
            }

            List<DataSnapshot> arr = new ArrayList<>();

            // Массив магазинов
            Iterable<DataSnapshot> items = task.getResult().getChildren();

            // Конвертация магазинов из Iterable<DataSnapshot> в List<DataSnapshot>
            for (DataSnapshot dataSnapshot : items) {
                arr.add(dataSnapshot);
            }

            // Запускаем фрагмент магазинов
            replaceFragment(new AdminShopsFragment(
                    arr,
                    getSupportFragmentManager()),
                    getSupportFragmentManager());
        });
    }

    public void openRests() {
        // Ссылка на рестораны
        DatabaseReference restsRef = databaseReference.child("Restaurants");

        // Получаем все рестораны и "прослушиваем" результат
        restsRef.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                // Соединиться с базой не удалось
                Toast.makeText(this, "Плохое соединение с базой", Toast.LENGTH_SHORT).show();
                return;
            }

            List<DataSnapshot> arr = new ArrayList<>();

            // Массив ресторанов
            Iterable<DataSnapshot> items = task.getResult().getChildren();

            // Конвертация ресторанов из Iterable<DataSnapshot> в List<DataSnapshot>
            for (DataSnapshot dataSnapshot : items) {
                arr.add(dataSnapshot);
            }

            // Запускаем фрагмент ресторанов
            replaceFragment(new AdminRestaurantsFragment(
                    arr,
                    getSupportFragmentManager()),
                    getSupportFragmentManager());
        });
    }

    public void openProfile() {
        // Запускаем фрагмент профиля
        replaceFragment(
                new AdminProfileFragment(getSupportFragmentManager()),
                getSupportFragmentManager());
    }

    // Метод для запуска фрагментов
    public static void replaceFragment(Fragment fragment, FragmentManager fragmentManager) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.a_admin_fragment, fragment);
        fragmentTransaction.commit();
    }

    // Слушатель результатов выбора картинки из галереи
    @Override
        protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && data != null && data.getData() != null) {
            if (resultCode == RESULT_OK) {
                // Загружаем картинку в базу данных
                uploadShopImage(data.getData(), getSharedPreferences("Clicked", MODE_PRIVATE).getString("Image", "Shop1"));
            }
        } else if (requestCode == 2 && data != null && data.getData() != null) {
            if (resultCode == RESULT_OK) {
                // Загружаем картинку в базу данных
                uploadRestImage(data.getData(), getSharedPreferences("Clicked", MODE_PRIVATE).getString("ImageRest", "Rest1"));
            }
        }
    }

    // Загрузка картинки магазина в базу данных
    private void uploadShopImage(Uri uri, String shopName) {
        try {
            // Получаем картинку из галереи по uri (ссылке)
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);

            // Создаем новый поток байтов
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            // Сжимаем картинку с помощью jpeg
            bitmap.compress(Bitmap.CompressFormat.JPEG, 1, byteArrayOutputStream);

            byte[] byteArray = byteArrayOutputStream.toByteArray();

            // Получаем название картинки из ссылки
            String path = uri.getPath();
            int index = path.lastIndexOf('/');
            if (index != -1) {
                path = path.substring(index + 1);
            }
            String imageName = path + System.currentTimeMillis();

            // Ссылка на магазины
            StorageReference firebaseStorage = FirebaseStorage.getInstance().getReference("Shops");
            DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference();

            // Загружаем картинку
            StorageReference mRef = firebaseStorage.child(imageName + ".jpg");
            UploadTask uploadTask = mRef.putBytes(byteArray);
            Task<Uri> task = uploadTask.continueWithTask(task1 -> mRef.getDownloadUrl()).addOnCompleteListener(task12 -> {
                if (task12.isSuccessful()) {
                    // Картинка успешно загружена. Изменяем ссылку на картинку в магазине
                    firebaseDatabase.child("Shops").child(shopName).child("Photo path").setValue(imageName);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Загрузка картинки ресторана в базу данных
    private void uploadRestImage(Uri uri, String restName) {
        try {
            // Получаем картинку из галереи по uri (ссылке)
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);

            // Создаем новый поток байтов
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            // Сжимаем картинку с помощью jpeg
            bitmap.compress(Bitmap.CompressFormat.JPEG, 1, byteArrayOutputStream);

            byte[] byteArray = byteArrayOutputStream.toByteArray();

            // Получаем название картинки из ссылки
            String path = uri.getPath();
            int index = path.lastIndexOf('/');
            if (index != -1) {
                path = path.substring(index + 1);
            }
            String imageName = path + System.currentTimeMillis();

            // Ссылка на рестораны
            StorageReference firebaseStorage = FirebaseStorage.getInstance().getReference("Restaurants");
            DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference();

            // Загружаем картинку
            StorageReference mRef = firebaseStorage.child(imageName + ".jpg");
            UploadTask uploadTask = mRef.putBytes(byteArray);
            Task<Uri> task = uploadTask.continueWithTask(task1 -> mRef.getDownloadUrl()).addOnCompleteListener(task12 -> {
                if (task12.isSuccessful()) {
                    // Картинка успешно загружена. Изменяем ссылку на картинку в ресторане
                    firebaseDatabase.child("Restaurants").child(restName).child("Photo path").setValue(imageName);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {

    }
}