package main.java.ZhenyaShvyrkov.javacore.jdbc.view;

import main.java.ZhenyaShvyrkov.javacore.jdbc.controller.SpecialtyController;
import main.java.ZhenyaShvyrkov.javacore.jdbc.model.Specialty;

import java.util.Scanner;

public class SpecialtyView extends View<Specialty> {
    private static SpecialtyView specialtyView;

    private SpecialtyView() {}

    public static synchronized SpecialtyView getSpecialtyView(){
        if (specialtyView == null) {
            specialtyView = new SpecialtyView();
            specialtyView.setController(SpecialtyController.getSpecialtyController());
        }
        return specialtyView;
    }

    @Override
    public void getRequest() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Create Read Update Delete:\n-----------------------------\n" +
                "* to create a new specialty: -c, name\n" +
                "* to read all specialties: -r\n" +
                "* to read a specialty by id: -r, id\n" +
                "* to update the specialty: -u, id, name\n" +
                "* to delete the specialty: -d, name\n" +
                "* to delete specialty by id: -d, id\n" +
                "->: ");
        String request;
        while ((request = scanner.nextLine()) != null && request.length() != 0){
            String[] accountData = controller.handleRequest(request);
            separateData(accountData);
            System.out.print("->: ");
        }
        scanner.close();
    }

    @Override
    public void separateData(String[] accountData) {
        String command = accountData[0];
        String name;
        long id;
        switch (command) {
            case "-c":
                name = accountData[1];
                response(controller.create(new Specialty(name)));
                break;
            case "-r":
                if (accountData.length > 1) {
                    id = Long.parseLong(accountData[1]);
                    response(controller.readById(id));
                } else {
                    response(controller.read());
                }
                break;
            case "-u":
                id = Long.parseLong(accountData[1]);
                name = accountData[2];
                response(controller.update(id, new Specialty(name)));
                break;
            case "-d":
                if (isNumber(accountData[1])) {
                    controller.deleteByID(Long.parseLong(accountData[1]));
                } else {
                    controller.delete(new Specialty(accountData[1]));
                }
                response("Successfully deleted");
                break;
            default:
                throw new UnsupportedOperationException();
        }
    }
    public boolean isNumber(String str){
        try{
            Long.parseLong(str);
            return true;
        } catch(Exception e) {
            return false;
        }
    }
}

