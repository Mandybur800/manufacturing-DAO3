package com.manufacturing.controllers.car;

import com.manufacturing.lib.Injector;
import com.manufacturing.model.Car;
import com.manufacturing.model.Driver;
import com.manufacturing.service.CarService;
import com.manufacturing.service.DriverService;
import java.io.IOException;
import java.util.NoSuchElementException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AddDriverToCarController extends HttpServlet {
    private static final Injector injector = Injector.getInstance("com.manufacturing");
    private final CarService carService = (CarService) injector.getInstance(CarService.class);
    private final DriverService driverService = (DriverService)
            injector.getInstance(DriverService.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/car/drivers/add.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        try {
            Long driverId = Long.valueOf(req.getParameter("driver_id"));
            Long carId = Long.valueOf(req.getParameter("car_id"));
            Car car = carService.get(carId);
            Driver driver = driverService.get(driverId);
            carService.addDriverToCar(driver,car);
            resp.sendRedirect(req.getContextPath() + "/");
        } catch (NoSuchElementException e) {
            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/incorrect.jsp").forward(req, resp);
        }
    }
}
