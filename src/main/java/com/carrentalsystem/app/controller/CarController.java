package com.carrentalsystem.app.controller;

import com.carrentalsystem.app.dto.CarDTO;
import com.carrentalsystem.app.dto.CarUploadDTO;
import com.carrentalsystem.app.entity.CarImage;
import com.carrentalsystem.app.helper.CarType;
import com.carrentalsystem.app.helper.FuelType;
import com.carrentalsystem.app.service.CarService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/cars")
public class CarController {

    private final CarService carService;

    @GetMapping
    public String listCars(Model model) {
        List<CarDTO> cars = carService.getAllCars();
        model.addAttribute("cars", cars);
        return "admin/cars";
    }


    @GetMapping("/add")
    public String showAddCarForm(Model model) {
        model.addAttribute("car", new CarUploadDTO());
        model.addAttribute("fuelTypes", FuelType.values());
        model.addAttribute("type",  CarType.values());
        model.addAttribute("imageFiles", List.of());
        return "admin/addcar";
    }

    @PostMapping("/add")
    public String addCar(@Valid @ModelAttribute("car") CarUploadDTO carDTO,
                         BindingResult result,
                         @RequestParam("imageFiles") List<MultipartFile> imageFiles,
                         RedirectAttributes redirectAttributes,
                         Model model) {

        if (result.hasErrors()) {
            model.addAttribute("fuelTypes", FuelType.values());
            return "admin/addcar";
        }

        try {
            carService.addCarWithImages(carDTO, imageFiles);
            redirectAttributes.addFlashAttribute("success", "Car added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to add car: " + e.getMessage());
        }

        return "redirect:/admin/cars/add";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        CarDTO carDTO = carService.getCarById(id);
        model.addAttribute("car", carDTO);
        model.addAttribute("carId", id);
        model.addAttribute("CarType", CarType.values());
        model.addAttribute("fuelTypes", FuelType.values());
        return "admin/edit";
    }

    @PostMapping("/edit/{id}")
    public String updateCar(@PathVariable("id") Integer id,
                            @Valid @ModelAttribute("car") CarUploadDTO carDTO,
                            BindingResult result,
                            @RequestParam(value = "imageFiles", required = false) List<MultipartFile> imageFiles,
                            RedirectAttributes redirectAttributes,
                            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("fuelTypes", FuelType.values());
            model.addAttribute("carId", id);
            return "admin/edit";
        }

        try {
            carService.updateCar(id, carDTO);
            if (imageFiles != null && !imageFiles.isEmpty()) {
                carService.addCarWithImages(id, imageFiles);
            }
            redirectAttributes.addFlashAttribute("success", "Car updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Update failed: " + e.getMessage());
        }

        return "redirect:/admin/cars/edit/" + id;
    }

    @GetMapping("/view/{id}")
    public String viewCarDetails(@PathVariable("id") Integer id, Model model) {
        CarDTO car = carService.getCarById(id);
        model.addAttribute("car", car);
        return "admin/view";
    }

    @GetMapping("/delete/{id}")
    public String deleteCar(@PathVariable("id") Integer id,
                            RedirectAttributes redirectAttributes) {
        try {
            carService.deleteCar(id);
            redirectAttributes.addFlashAttribute("success", "Car deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete car: " + e.getMessage());
        }

        return "redirect:/admin/cars";
    }

    @GetMapping("/delete-image")
    public String deleteCarImage(@RequestParam("carId") Integer carId,
                                 @RequestParam("url") String imageUrl,
                                 RedirectAttributes redirectAttributes) {
        try {
            CarImage image = carService.findByImageUrl(imageUrl);
            String fileName = imageUrl.replace("/uploads/", "");
            Path filePath = Paths.get("uploads").resolve(fileName);
            Files.deleteIfExists(filePath);
            carService.deleteImage(image);
            redirectAttributes.addFlashAttribute("success", "Image deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete image: " + e.getMessage());
        }

        return "redirect:/admin/cars/edit/" + carId;
    }
}
