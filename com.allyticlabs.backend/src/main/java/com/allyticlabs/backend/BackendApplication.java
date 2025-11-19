package com.allyticlabs.backend;

import com.allyticlabs.backend.model.Contact;
import com.allyticlabs.backend.model.Drone;
import com.allyticlabs.backend.model.Newsletter;
import com.allyticlabs.backend.model.Robot;
import com.allyticlabs.backend.model.SolarPanel;
import com.allyticlabs.backend.repository.ContactRepository;
import com.allyticlabs.backend.repository.DroneRepository;
import com.allyticlabs.backend.repository.NewsletterRepository;
import com.allyticlabs.backend.repository.RobotRepository;
import com.allyticlabs.backend.repository.SolarPanelRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

    @Bean
    public CommandLineRunner initData(
            RobotRepository robotRepository,
            DroneRepository droneRepository,
            SolarPanelRepository solarPanelRepository,
            ContactRepository contactRepository,
            NewsletterRepository newsletterRepository) {
        return args -> {
            // Seed Robots
            if (robotRepository.findById("1") == null) {
                Robot robot1 = new Robot();
                robot1.setId("1");
                robot1.setName("NeuroBot X1");
                robot1.setType("AI Assistant");
                robot1.setDescription("Advanced neural network processing with human-like conversation abilities and emotional intelligence.");
                robot1.setCapabilities(Arrays.asList("Natural Language Processing", "Emotional Recognition", "Learning Adaptation", "Multi-task Execution"));
                robot1.setImage("https://images.pexels.com/photos/8386440/pexels-photo-8386440.jpeg?auto=compress&cs=tinysrgb&w=400");
                robot1.setPrice("$12,999");
                robot1.setRating(4.9);
                robot1.setReviews(1247);
                robotRepository.save(robot1);
            }

            if (robotRepository.findById("2") == null) {
                Robot robot2 = new Robot();
                robot2.setId("2");
                robot2.setName("IndustroMax Pro");
                robot2.setType("Industrial Robot");
                robot2.setDescription("Heavy-duty manufacturing robot with precision control and 24/7 operational capability.");
                robot2.setCapabilities(Arrays.asList("Precision Assembly", "Quality Control", "Heavy Lifting", "Continuous Operation"));
                robot2.setImage("https://images.pexels.com/photos/2085831/pexels-photo-2085831.jpeg?auto=compress&cs=tinysrgb&w=400");
                robot2.setPrice("$45,000");
                robot2.setRating(4.8);
                robot2.setReviews(892);
                robotRepository.save(robot2);
            }

            if (robotRepository.findById("3") == null) {
                Robot robot3 = new Robot();
                robot3.setId("3");
                robot3.setName("CompanionBot Mini");
                robot3.setType("Personal Assistant");
                robot3.setDescription("Compact home companion with smart home integration and personalized care features.");
                robot3.setCapabilities(Arrays.asList("Smart Home Control", "Health Monitoring", "Entertainment", "Security Alerts"));
                robot3.setImage("https://images.pexels.com/photos/8439093/pexels-photo-8439093.jpeg?auto=compress&cs=tinysrgb&w=400");
                robot3.setPrice("$2,499");
                robot3.setRating(4.7);
                robot3.setReviews(2156);
                robotRepository.save(robot3);
            }

            // Seed Drones
            if (droneRepository.findById("1") == null) {
                Drone drone1 = new Drone();
                drone1.setId("1");
                drone1.setName("AeroVision Pro 4K");
                drone1.setType("Photography Drone");
                drone1.setDescription("Professional-grade aerial photography drone with 4K HDR camera and advanced stabilization for stunning cinematic shots.");
                drone1.setSpecifications(Arrays.asList("4K HDR Camera", "3-Axis Gimbal", "Obstacle Avoidance", "Follow Me Mode"));
                drone1.setImage("https://images.pexels.com/photos/442587/pexels-photo-442587.jpeg?auto=compress&cs=tinysrgb&w=400");
                drone1.setPrice("$1,299");
                drone1.setRating(4.8);
                drone1.setReviews(2341);
                drone1.setFlightTime("34 min");
                drone1.setRange("8 km");
                droneRepository.save(drone1);
            }

            if (droneRepository.findById("2") == null) {
                Drone drone2 = new Drone();
                drone2.setId("2");
                drone2.setName("CargoLift Heavy");
                drone2.setType("Delivery Drone");
                drone2.setDescription("Industrial delivery drone capable of carrying heavy payloads across long distances with precision GPS navigation.");
                drone2.setSpecifications(Arrays.asList("50kg Payload", "GPS Navigation", "Weather Resistant", "Auto Landing"));
                drone2.setImage("https://images.pexels.com/photos/8566473/pexels-photo-8566473.jpeg?auto=compress&cs=tinysrgb&w=400");
                drone2.setPrice("$15,999");
                drone2.setRating(4.7);
                drone2.setReviews(892);
                drone2.setFlightTime("45 min");
                drone2.setRange("25 km");
                droneRepository.save(drone2);
            }

            if (droneRepository.findById("3") == null) {
                Drone drone3 = new Drone();
                drone3.setId("3");
                drone3.setName("RaceFly Speed X");
                drone3.setType("Racing Drone");
                drone3.setDescription("High-performance racing drone built for speed and agility with carbon fiber frame and responsive controls.");
                drone3.setSpecifications(Arrays.asList("Carbon Fiber Frame", "High-Speed Motors", "FPV Camera", "Acrobatic Mode"));
                drone3.setImage("https://images.pexels.com/photos/724921/pexels-photo-724921.jpeg?auto=compress&cs=tinysrgb&w=400");
                drone3.setPrice("$899");
                drone3.setRating(4.9);
                drone3.setReviews(1567);
                drone3.setFlightTime("12 min");
                drone3.setRange("2 km");
                droneRepository.save(drone3);
            }

            // Seed SolarPanels
            if (solarPanelRepository.findById("1") == null) {
                SolarPanel solarPanel1 = new SolarPanel();
                solarPanel1.setId("1");
                solarPanel1.setName("SolarMax Home Pro");
                solarPanel1.setType("residential");
                solarPanel1.setDescription("Perfect for residential rooftops with high efficiency and reliable performance.");
                solarPanel1.setFeatures(Arrays.asList("Monocrystalline silicon", "Weather resistant", "Easy installation", "Smart monitoring"));
                solarPanel1.setImage("🏠"); // Emoji as in frontend; replace with URL if needed
                solarPanel1.setPower("400W");
                solarPanel1.setEfficiency("22.1%");
                solarPanel1.setWarranty("25 years");
                solarPanel1.setPrice("$299");
                solarPanelRepository.save(solarPanel1);
            }

            if (solarPanelRepository.findById("2") == null) {
                SolarPanel solarPanel2 = new SolarPanel();
                solarPanel2.setId("2");
                solarPanel2.setName("EcoSolar Residential");
                solarPanel2.setType("residential");
                solarPanel2.setDescription("Budget-friendly option without compromising on quality and durability.");
                solarPanel2.setFeatures(Arrays.asList("Polycrystalline silicon", "Cost-effective", "Durable frame", "Grid-tie compatible"));
                solarPanel2.setImage("🏡");
                solarPanel2.setPower("350W");
                solarPanel2.setEfficiency("20.5%");
                solarPanel2.setWarranty("20 years");
                solarPanel2.setPrice("$249");
                solarPanelRepository.save(solarPanel2);
            }

            if (solarPanelRepository.findById("3") == null) {
                SolarPanel solarPanel3 = new SolarPanel();
                solarPanel3.setId("3");
                solarPanel3.setName("PowerGrid Commercial");
                solarPanel3.setType("commercial");
                solarPanel3.setDescription("Industrial-grade solar panels designed for large-scale commercial installations.");
                solarPanel3.setFeatures(Arrays.asList("High-power output", "Commercial grade", "Scalable systems", "Remote monitoring"));
                solarPanel3.setImage("🏢");
                solarPanel3.setPower("500W");
                solarPanel3.setEfficiency("23.5%");
                solarPanel3.setWarranty("25 years");
                solarPanel3.setPrice("$399");
                solarPanelRepository.save(solarPanel3);
            }

            // Seed a sample Contact (for testing)
            if (contactRepository.findAll().isEmpty()) {
                Contact contact = new Contact();
                contact.setId("1");
                contact.setFirstName("John");
                contact.setLastName("Doe");
                contact.setEmail("john.doe@example.com");
                contact.setHelpType("Technical support");
                contact.setMessage("Need help with drone setup.");
                contactRepository.save(contact);
            }

            // Seed a sample Newsletter (for testing)
            if (newsletterRepository.findAll().isEmpty()) {
                Newsletter newsletter = new Newsletter();
                newsletter.setEmail("user@example.com");
                newsletter.setSubscribedAt(LocalDateTime.now());
                newsletterRepository.save(newsletter);
            }

            System.out.println("Initial data seeded successfully!");
        };
    }
}