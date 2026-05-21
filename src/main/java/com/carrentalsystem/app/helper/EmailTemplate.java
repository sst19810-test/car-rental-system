package com.carrentalsystem.app.helper;

import com.carrentalsystem.app.entity.Booking;
import com.carrentalsystem.app.entity.Car;
import com.carrentalsystem.app.entity.User;

public class EmailTemplate {

    public static String buildBookingConfirmationEmail(User user, Booking booking) {
        Car car = booking.getCar();

        return """
        <html>
        <body style="font-family: Arial, sans-serif; background-color: #f7f7f7; padding: 0; margin: 0;">
            <div style="max-width: 600px; margin: 20px auto; background-color: white; border-radius: 10px; box-shadow: 0 0 10px rgba(0,0,0,0.1); overflow: hidden;">
                <div style="background-color: #007bff; color: white; padding: 20px; text-align: center;">
                    <h2 style="margin: 0;">RideVia Booking Confirmation 🚗</h2>
                </div>
                <div style="padding: 25px; color: #333;">
                    <p>Hi <b>%s</b>,</p>
                    <p>Your booking has been successfully created! Below are your booking details:</p>

                    <table style="width: 100%%; border-collapse: collapse; margin-top: 10px;">
                        <tr>
                            <td style="padding: 8px; border-bottom: 1px solid #eee;"><b>Booking ID:</b></td>
                            <td style="padding: 8px; border-bottom: 1px solid #eee;">%d</td>
                        </tr>
                        <tr>
                            <td style="padding: 8px; border-bottom: 1px solid #eee;"><b>Car:</b></td>
                            <td style="padding: 8px; border-bottom: 1px solid #eee;">%s</td>
                        </tr>
                        <tr>
                            <td style="padding: 8px; border-bottom: 1px solid #eee;"><b>Start Time:</b></td>
                            <td style="padding: 8px; border-bottom: 1px solid #eee;">%s</td>
                        </tr>
                        <tr>
                            <td style="padding: 8px; border-bottom: 1px solid #eee;"><b>End Time:</b></td>
                            <td style="padding: 8px; border-bottom: 1px solid #eee;">%s</td>
                        </tr>
                        <tr>
                            <td style="padding: 8px; border-bottom: 1px solid #eee;"><b>Total Price:</b></td>
                            <td style="padding: 8px; border-bottom: 1px solid #eee;">₹%.2f</td>
                        </tr>
                        <tr>
                            <td style="padding: 8px;"><b>Status:</b></td>
                            <td style="padding: 8px;">%s</td>
                        </tr>
                    </table>

                    <p style="margin-top: 20px;">We’ll notify you once your booking is confirmed by the admin.</p>
                    <p>Thank you for choosing <b>RideVia</b> — Rent. Ride. Repeat.</p>

                    <div style="margin-top: 30px; text-align: center;">
                        <a href="https://ridevia.com/bookings" 
                           style="background-color: #007bff; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;">
                           View My Bookings
                        </a>
                    </div>
                </div>

                <div style="background-color: #f0f0f0; color: #666; padding: 15px; text-align: center; font-size: 13px;">
                    © 2025 RideVia. All rights reserved.<br>
                    <a href="https://ridevia.com" style="color: #007bff; text-decoration: none;">Visit our website</a>
                </div>
            </div>
        </body>
        </html>
        """.formatted(
                user.getName(),
                booking.getId(),
                car.getBrand(),
                car.getModel(),
                booking.getStartTime(),
                booking.getEndTime(),
                booking.getTotalPrice(),
                booking.getStatus()
        );
    }

}
