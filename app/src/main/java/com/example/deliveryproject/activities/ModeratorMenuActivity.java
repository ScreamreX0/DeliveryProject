package com.example.deliveryproject.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import com.example.deliveryproject.R;
import com.example.deliveryproject.fragments.AdminProfileFragment;
import com.example.deliveryproject.fragments.AdminShopsFragment;
import com.example.deliveryproject.fragments.ModerPositionsFragment;
import com.example.deliveryproject.fragments.UserProductsFragment;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
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

public class ModeratorMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moderator_menu);

        // По дефолту открываются магазины
        openPositions();

        // Настройка нижней панели навигации
        BottomNavigationView bottomNavigationMenuView = findViewById(R.id.a_moder_bottom_menu);
        bottomNavigationMenuView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.moder_bottom_positions) {
                openPositions();
            } else if (item.getItemId() == R.id.moder_bottom_account) {
                openProfile();
            }
            return true;
        });
    }

    // Ссылка на базу данных
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    // Слушатель результатов выбора картинки из галереи
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 3 && data != null && data.getData() != null) {
            if (resultCode == RESULT_OK) {
                String sharedPrefImage = getSharedPreferences("Clicked", MODE_PRIVATE)
                        .getString("ImagePosition", "");

                // Загружаем картинку в базу данных
                uploadPositionImage(
                        data.getData(),
                        sharedPrefImage.split(";")[0],
                        sharedPrefImage.split(";")[1],
                        sharedPrefImage.split(";")[2]);
            }
        }
    }

    // Загрузка картинки позиции в базу данных
    private void uploadPositionImage(Uri uri,
                                     String type,
                                     String name,
                                     String positionName) {
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

            // Ссылка на позиции
            StorageReference firebaseStorage = FirebaseStorage.getInstance().getReference();
            DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference();

            // Загружаем картинку
            StorageReference mRef = firebaseStorage.child(imageName + ".jpg");
            UploadTask uploadTask = mRef.putBytes(byteArray);
            Task<Uri> task = uploadTask.continueWithTask(task1 -> mRef.getDownloadUrl()).addOnCompleteListener(task12 -> {
                if (task12.isSuccessful()) {
                    String menuType = "Menu";
                    if (type.equals("Shops")) {
                        menuType = "Range";
                    }

                    // Картинка успешно загружена. Изменяем ссылку на картинку
                    firebaseDatabase.child(type)
                            .child(name)
                            .child(menuType)
                            .child(positionName)
                            .child("Photo path")
                            .setValue(imageName);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openPositions() {
        // Получение ссылки на настройки модератора из базы
        DatabaseReference moderSettings = databaseReference.child("Users")
                .child(firebaseAuth.getUid())
                .child("PrivilegedSettings");

        // Получение настроек модератора. Слушатель ответа от базы
        moderSettings.get().addOnCompleteListener(runnable -> {
            if (!runnable.isSuccessful()) {
                Toast.makeText(this, "Не удалось получить ответ от базы", Toast.LENGTH_SHORT).show();
                return;
            }

            // Результат ответа от базы
            DataSnapshot result = runnable.getResult();

            // Получение названия и типа заведения
            String name = result.child("Name").getValue().toString();
            String type = result.child("Type").getValue().toString();

            // Проверка на тип заведения
            if (type.equals("Shop")) {
                // Получение всех снимков всех магазинов из базы. Слушатель ответа от базы
                databaseReference.child("Shops").get().addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Toast.makeText(this, "Не удалось получить ответ от базы", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    for (DataSnapshot ds : task.getResult().getChildren()) {
                        if (ds.child("Name").getValue().toString().equals(name)) {
                            Iterable<DataSnapshot> items = ds.child("Range").getChildren();

                            ArrayList<DataSnapshot> arr = new ArrayList<>();
                            for (DataSnapshot snapshot : items) {
                                arr.add(snapshot);
                            }

                            FragmentManager fragmentManager = getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.a_moder_fragment, new ModerPositionsFragment(
                                    arr,
                                    getSupportFragmentManager(),
                                    "Shop",
                                    name));
                            fragmentTransaction.commit();
                            return;
                        }
                    }
                });
            } else if (type.equals("Rest") || type.equals("Restaurant")) {
                // Получение всех снимков всех ресторанов из базы. Слушатель ответа от базы
                databaseReference.child("Restaurants").get().addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Toast.makeText(this, "Не удалось получить ответ от базы", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    for (DataSnapshot ds : task.getResult().getChildren()) {
                        if (ds.child("Name").getValue().toString().equals(name)) {
                            Iterable<DataSnapshot> items = ds.child("Menu").getChildren();

                            ArrayList<DataSnapshot> arr = new ArrayList<>();
                            for (DataSnapshot snapshot : items) {
                                arr.add(snapshot);
                            }

                            FragmentManager fragmentManager = getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.a_moder_fragment, new ModerPositionsFragment(
                                    arr,
                                    getSupportFragmentManager(),
                                    "Restaurant",
                                    name));
                            fragmentTransaction.commit();
                            return;
                        }
                    }
                });
            } else {
                Toast.makeText(this, "Неверный тип заведения", Toast.LENGTH_SHORT).show();
                return;
            }
        });
    }

    private void openProfile() {
        // Запускаем фрагмент профиля

        // TODO: Добавить модератора
        replaceFragment(
                new AdminProfileFragment(getSupportFragmentManager()),
                getSupportFragmentManager());
    }

    // Метод для запуска фрагментов
    public static void replaceFragment(Fragment fragment, FragmentManager fragmentManager) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.a_moder_fragment, fragment);
        fragmentTransaction.commit();
    }
}