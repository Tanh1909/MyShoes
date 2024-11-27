package com.example.moduleapp;

import com.example.moduleapp.repository.impl.ProductRepository;
import com.example.moduleapp.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafka;

import java.util.List;
import java.util.Random;

@Slf4j
@EnableKafka
@SpringBootApplication(scanBasePackages = "com.example")
@RequiredArgsConstructor
public class ModuleAppApplication {
    private final ProductRepository productRepository;
    private final ProductService productService;

    public static void main(String[] args) {
        SpringApplication.run(ModuleAppApplication.class, args);
    }

    @Bean
    public ApplicationRunner applicationRunner() {
        return args -> {

        };
    }

//    @Bean
//    public ApplicationRunner applicationRunner() {
//        return args -> {
//            List<String> productNames = new ArrayList<>(List.of(
//                    // Giày Nike
//                    "Nike Air Max 270",
//                    "Nike ZoomX Invincible",
//                    "Nike Pegasus Trail",
//                    "Nike React Infinity",
//                    "Nike Free Run",
//                    "Nike Revolution",
//                    "Nike Vapormax",
//                    "Nike Joyride Dual Run",
//                    "Nike Air Zoom Tempo",
//                    "Nike Wildhorse",
//
//                    // Giày Adidas
//                    "Adidas Ultraboost Light",
//                    "Adidas NMD R1",
//                    "Adidas Samba OG",
//                    "Adidas Forum Low",
//                    "Adidas Adizero Adios",
//                    "Adidas Solar Glide",
//                    "Adidas Supernova",
//                    "Adidas Gazelle Vintage",
//                    "Adidas Ozelia",
//                    "Adidas 4D FWD",
//
//                    // Giày Lascode
//                    "Lascode Classic Edition",
//                    "Lascode Retro Style",
//                    "Lascode Street Step",
//                    "Lascode Urban Sprint",
//                    "Lascode Cloud Comfort",
//                    "Lascode Trail Tracker",
//                    "Lascode Aero Boost",
//                    "Lascode Dynamic Flex",
//                    "Lascode Fusion Sport",
//                    "Lascode Lightweight Speed",
//
//                    // Giày Puma
//                    "Puma RS-X Mono",
//                    "Puma Future Rider Luxe",
//                    "Puma Suede Classic",
//                    "Puma Wild Rider Rollin",
//                    "Puma Velocity Nitro",
//                    "Puma Magnify Nitro",
//                    "Puma Deviate Elite",
//                    "Puma Slipstream Lo",
//                    "Puma Fast-R Nitro",
//                    "Puma Rebound Joy",
//
//                    // Giày Asics
//                    "Asics Gel-Kayano",
//                    "Asics Gel-Nimbus",
//                    "Asics Gel-Quantum 360",
//                    "Asics Novablast",
//                    "Asics Metarun",
//                    "Asics GlideRide",
//                    "Asics EvoRide",
//                    "Asics Gel-Excite",
//                    "Asics Dynablast",
//                    "Asics Gel-Contend"
//            ));
//            List<Integer> categoryIds = List.of(3, 4, 5, 6, 7, 8);
//            ProductRequest.AttributeRequest attributeRequest = new ProductRequest.AttributeRequest();
//            attributeRequest.setName("kích cỡ");
//            attributeRequest.setOptions(Set.of("40", "41", "42", "43", "44", "45"));
//            for (int i = 0; i < 500; i++) {
//                ProductRequest productRequest = new ProductRequest();
//                productRequest.setName(getRandomElement(productNames));
//                productRequest.setDescription("Thiết kế đệm khí nổi bật của Nike");
//                productRequest.setCategoryId(getRandomElement(categoryIds));
//                productRequest.setAttributes(Set.of(attributeRequest));
//                List<ProductRequest.VariantsRequest> variantsRequests = new ArrayList<>();
//                Double price = randomPrice();
//                for (int j = 0; j < 6; j++) {
//                    ProductRequest.VariantsRequest variantsRequest = new ProductRequest.VariantsRequest();
//                    variantsRequest.setPrice(price);
//                    variantsRequest.setStock(randomQuantity());
//                    variantsRequest.setAttributeOptions(List.of((j + 40) + ""));
//                    variantsRequests.add(variantsRequest);
//                }
//                productRequest.setVariants(variantsRequests);
//                ImageRequest imageRequest = new ImageRequest();
//                imageRequest.setPrimary(true);
//                imageRequest.setId(20 + i);
//                productRequest.setImages(List.of(imageRequest));
//                productRequest.setPrice(price);
//                productService.create(productRequest);
//
//            }
//            System.out.println("finish");
//
//        };
//    }

    public static Double randomPrice() {
        Random random = new Random();
        int min = 100000;
        int max = 200000;
        int step = 1000; // Đảm bảo giá trị luôn là bội số của 1,000
        // Tạo giá trị ngẫu nhiên trong khoảng [min, max], đảm bảo bội số của step
        return (double) (min + (random.nextInt((max - min) / step + 1)) * step);
    }

    public static Integer randomQuantity() {
        Random random = new Random();
        int minStock = 100;
        int maxStock = 250;
        return random.nextInt(maxStock - minStock + 1) + minStock;
    }

    public static <T> T getRandomElement(List<T> list) {
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("List không được rỗng hoặc null");
        }
        Random random = new Random();
        int index = random.nextInt(list.size()); // Lấy chỉ số ngẫu nhiên từ 0 đến list.size() - 1
        return list.get(index);
    }

}
