package com.fixsure.config;

import com.fixsure.entity.*;
import com.fixsure.entity.Service;
import com.fixsure.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements ApplicationRunner {

        private final CategoryRepository categoryRepository;
        private final ServiceRepository serviceRepository;
        private final TechnicianRepository technicianRepository;
        private final TimeSlotRepository timeSlotRepository;

        @Override
        @Transactional
        public void run(ApplicationArguments args) {
                log.info("Seeding initial data...");
                seedTimeSlots();
                seedCategories();
                seedTechnicians();
                log.info("Data seeding complete.");
        }

        private void seedTimeSlots() {
                if (timeSlotRepository.count() > 0)
                        return;
                timeSlotRepository.saveAll(List.of(
                                TimeSlot.builder().label("Morning").startTime(LocalTime.of(8, 0))
                                                .endTime(LocalTime.of(12, 0)).build(),
                                TimeSlot.builder().label("Afternoon").startTime(LocalTime.of(12, 0))
                                                .endTime(LocalTime.of(17, 0))
                                                .build(),
                                TimeSlot.builder().label("Evening").startTime(LocalTime.of(17, 0))
                                                .endTime(LocalTime.of(20, 0))
                                                .build()));
                log.info("Seeded 3 time slots");
        }

        private void seedCategories() {
                if (categoryRepository.count() > 0)
                        return;

                Category ac = saveCategory("AC Service", "Air conditioner installation, repair & maintenance",
                                "https://cdn-icons-png.flaticon.com/512/2933/2933116.png");
                Category ro = saveCategory("RO Service", "RO water purifier service & repair",
                                "https://cdn-icons-png.flaticon.com/512/1170/1170678.png");
                Category cctv = saveCategory("CCTV", "CCTV camera installation and configuration",
                                "https://cdn-icons-png.flaticon.com/512/3534/3534038.png");
                Category cleaning = saveCategory("Cleaning", "Home deep cleaning and sanitisation",
                                "https://cdn-icons-png.flaticon.com/512/995/995016.png");
                Category plumbing = saveCategory("Plumbing", "Pipe, leak, tap & bathroom fittings",
                                "https://cdn-icons-png.flaticon.com/512/2933/2933185.png");
                Category electrical = saveCategory("Electrical", "Wiring, switches, fans & fixture installation",
                                "https://cdn-icons-png.flaticon.com/512/1283/1283192.png");

                // AC Services
                saveService(ac, "AC Deep Cleaning",
                                "Complete deep cleaning of AC filters, coils, and drain.",
                                null, 899, 90, "4.8", 1240,
                                List.of("Filter washing", "Coil cleaning", "Drain pipe flush", "Gas pressure check"));

                saveService(ac, "AC Gas Refill",
                                "AC refrigerant gas refilling with pressure testing.",
                                null, 1299, 60, "4.6", 876,
                                List.of("Gas pressure test", "Leak detection", "Refrigerant refill",
                                                "Performance check"));

                saveService(ac, "AC Installation",
                                "New AC unit installation with demo.",
                                null, 1499, 120, "4.7", 540,
                                List.of("Mounting bracket", "Copper piping", "Electrical connection",
                                                "Testing & demo"));

                // RO Services
                saveService(ro, "RO Full Service",
                                "Complete RO system service with filter replacement.",
                                null, 499, 60, "4.5", 723,
                                List.of("Filter replacement", "Membrane check", "Sanitisation", "TDS calibration"));

                saveService(ro, "RO Installation",
                                "New RO water purifier installation.",
                                null, 799, 90, "4.6", 312,
                                List.of("Wall mounting", "Inlet connection", "Storage tank setup", "Test run"));

                // CCTV Services
                saveService(cctv, "CCTV Camera Installation",
                                "Install security cameras at home or office.",
                                null, 1999, 180, "4.7", 428,
                                List.of("Camera positioning", "Cable routing", "DVR/NVR setup", "Mobile app config"));

                saveService(cctv, "CCTV Maintenance",
                                "Annual maintenance and health check of existing CCTV.",
                                null, 699, 60, "4.4", 198,
                                List.of("Lens cleaning", "Cable check", "Recording test", "Night vision test"));

                // Cleaning Services
                saveService(cleaning, "Home Deep Cleaning",
                                "Full-home professional deep cleaning.",
                                null, 2499, 240, "4.9", 1870,
                                List.of("Kitchen de-greasing", "Bathroom scrubbing", "Floor mopping",
                                                "Window cleaning"));

                saveService(cleaning, "Bathroom Cleaning",
                                "Deep cleaning of bathrooms with disinfection.",
                                null, 699, 90, "4.7", 945,
                                List.of("Tile scrubbing", "Disinfection", "Tap polishing", "Mirror cleaning"));

                // Plumbing Services
                saveService(plumbing, "Tap & Faucet Repair",
                                "Fix leaking taps and replace faucets.",
                                null, 299, 45, "4.5", 654,
                                List.of("Leak inspection", "Washer replacement", "Faucet fitting", "Flow testing"));

                saveService(plumbing, "Drain Unclogging",
                                "Clear blocked drains with professional tools.",
                                null, 399, 60, "4.3", 432,
                                List.of("Blockage inspection", "Drain snake/jet", "Flush test", "Preventive advice"));

                // Electrical Services
                saveService(electrical, "Fan Installation",
                                "Ceiling or wall fan installation.",
                                null, 249, 45, "4.6", 780,
                                List.of("Mounting bracket", "Wiring connection", "Regulator fitting", "Test run"));

                saveService(electrical, "Switch & Socket Repair",
                                "Repair or replace faulty switches and sockets.",
                                null, 199, 30, "4.5", 560,
                                List.of("Fault diagnosis", "Part replacement", "Safety check", "Insulation test"));

                log.info("Seeded 6 categories and 13 services");
        }

        private void seedTechnicians() {
                if (technicianRepository.count() > 0)
                        return;
                technicianRepository.saveAll(List.of(
                                Technician.builder().name("Ramesh Kumar").phone("9876543210")
                                                .email("ramesh@fixsure.com").specializations("AC,Electrical")
                                                .isAvailable(true).build(),
                                Technician.builder().name("Suresh Patel").phone("9123456789")
                                                .email("suresh@fixsure.com").specializations("RO,Plumbing")
                                                .isAvailable(true).build(),
                                Technician.builder().name("Anita Singh").phone("9988776655")
                                                .email("anita@fixsure.com").specializations("CCTV,Cleaning")
                                                .isAvailable(true).build()));
                log.info("Seeded 3 technicians");
        }

        // ---- Helpers ----

        private Category saveCategory(String name, String description, String iconUrl) {
                return categoryRepository.save(Category.builder()
                                .name(name).description(description).iconUrl(iconUrl).build());
        }

        private void saveService(Category category, String name, String description,
                        String imageUrl, int basePrice, int durationMinutes,
                        String rating, int reviewCount, List<String> includes) {
                Service service = Service.builder()
                                .category(category)
                                .name(name)
                                .description(description)
                                .imageUrl(imageUrl)
                                .basePrice(new BigDecimal(basePrice))
                                .durationMinutes(durationMinutes)
                                .rating(new BigDecimal(rating))
                                .reviewCount(reviewCount)
                                .includes(String.join(",", includes))
                                .build();

                serviceRepository.save(service);
        }
}
