import java.util.*;

enum DepositType {
    SAVINGS("Сберегательный", 7.5, 3),
    FIXED_TERM("Срочный", 5, 1),
    CUMULATIVE("Накопительный", 9.0, 5);

    private final String description;
    private final double interestRate;
    private final int years;

    DepositType(String description, double interestRate, int years) {
        this.description = description;
        this.interestRate = interestRate;
        this.years = years;
    }

    public String getDescription() {
        return description;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public int getYears() {
        return years;
    }

    @Override
    public String toString() {
        return description + " (" + interestRate + "%)";
    }
}

 class Deposit {
     private DepositType type;
     private double initialAmount;

     // Геттеры

     public DepositType getType() {
         return type;
     }

     public double getInitialAmount() {
         return initialAmount;
     }

     Deposit(DepositType type, double initialAmount) {
         this.type = type;
         this.initialAmount = initialAmount;

     }

     public void topUpDeposit(double amount) {
         initialAmount += amount;
     }


     public double calculateFutureAmount() {
         double principal = initialAmount;
         double rate = type.getInterestRate() / 100;
         int years = type.getYears();

         return principal * Math.pow(1 + rate, years);
     }


 }
 class Client {
    private String name;
    private Deposit deposit;
    private String passportNumber;

    public String getName() {
        return name;
    }
    public Deposit getDeposit() {
        return deposit;
    }
    public String getPassportNumber() {
        return passportNumber;
    }
     public String getClientInfo() {
         return "Клиент: " + name +
                 ", Паспорт: " + passportNumber + "\n" +
                 "Вклад: " + deposit.getType().getDescription() +  // ← цепочка!
                 " на " + deposit.getInitialAmount() + " руб.";
     }
    Client(String passportNumber, String name, Deposit deposit) {
        this.passportNumber = passportNumber;
        this.name = name;
        this.deposit = deposit;
    }
 }
 class Bank {
     public void clearResources() {
         if (clients != null) {
             clients.clear();
             System.out.println("Ресурсы банка очищены.");
         }
     }
     private static Bank bank;
     private List<Client> clients;

     private Bank() {
         this.clients = new ArrayList<>();
     }

     public static Bank getBank() {
         if (bank == null) bank = new Bank();
         return bank;
     }

     public void addClient(Client client) {
         clients.add(client);
     }


     public List<Client> getAllClients() {
         return clients;
     }

     public void topUpDeposit(String passportNumber, double amount) {
         Client client = findClientByPassport(passportNumber);
         if (client != null) {
             client.getDeposit().topUpDeposit(amount);
         } else {
             throw new IllegalArgumentException("Клиент не найден");
         }
     }

     public double calculateTotalPayouts() {
         double total = 0;
         for (Client client : clients) {
                 total += client.getDeposit().calculateFutureAmount();
         }
         return Math.round(total * 10.0) / 10.0;
     }
     public Client findClientByPassport(String passportNumber) {
         for (Client client : clients) {
             if (client.getPassportNumber().equals(passportNumber)) {
                 return client;
             }
         }
         return null;
     }
 }


public class Main {
    private static boolean isPassportExists(Bank bank, String passport) {
        for (Client client : bank.getAllClients()) {
            if (client.getPassportNumber().equals(passport)) {
                return true;
            }
        }
        return false;
    }
    public static void main(String[] args) {
        Bank bank = Bank.getBank();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("=== БАНКОВСКАЯ СИСТЕМА ===");
            System.out.println("1. Добавить клиента");
            System.out.println("2. Показать всех клиентов");
            System.out.println("3. Пополнить вклад");
            System.out.println("4. Общая сумма выплат");
            System.out.println("5. Выйти");
            System.out.print("Выберите действие: ");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:

                        try {

                            String passport = "";
                            while (true) {
                                try {
                                    System.out.print("Введите номер паспорта (10 цифр): ");
                                    passport = scanner.nextLine().trim();


                                    if (!passport.matches("\\d{10}")) {
                                        System.out.println("Ошибка! Паспорт должен содержать ровно 10 цифр.");
                                        continue;
                                    }
                                    if (isPassportExists(bank, passport)) {
                                        System.out.println("Клиент с таким паспортом уже есть! ");
                                        continue;
                                    }


                                    break;

                                } catch (Exception e) {
                                    System.out.println("Ошибка ввода: " + e.getMessage());
                                }
                            }

                            System.out.print("Введите ФИО: ");
                            String name = scanner.nextLine();


                            DepositType type = null;
                            while (type == null) {
                                System.out.print("Введите тип вклада (SAVINGS/FIXED_TERM/CUMULATIVE): ");
                                String typeInput = scanner.nextLine().toUpperCase();
                                try {
                                    type = DepositType.valueOf(typeInput);
                                } catch (IllegalArgumentException e) {
                                    System.out.println("Ошибка! Неверный тип вклада. Используйте: SAVINGS, FIXED_TERM или CUMULATIVE");
                                }
                            }


                            double amount;
                            while (true) {
                                System.out.print("Введите начальную сумму: ");
                                String amountInput = scanner.nextLine().trim();
                                if (amountInput.endsWith(",") || amountInput.contains(",,") || amountInput.endsWith(".") || amountInput.contains("..")) {
                                    System.out.println("Ошибка! После запятой должна быть десятичная часть. Попробуйте снова.");
                                    continue;
                                }

                                amountInput = amountInput.replace(',', '.');
                                try {
                                    amount = Double.parseDouble(amountInput);
                                    if (amount > 0 && amount < 1000000000) {
                                        break;
                                    }
                                    else if(amount > 1000000000) {
                                        System.out.println("Сумма слишком велика! Введите сумму, меньшую, чем 1000000000");
                                    }
                                    else if (amount <= 0){
                                        System.out.println("Ошибка! Сумма должна быть больше либо равна 1. Попробуйте снова.");
                                    }
                                } catch (NumberFormatException e) {
                                    System.out.println("Ошибка! Введите корректное число (например: 1000 или 1500.50).");
                                }

                            }

                            Deposit deposit = new Deposit(type, amount);
                            Client client = new Client(passport, name, deposit);

                            bank.addClient(client);
                            System.out.println("Клиент успешно добавлен!");
                        } catch (Exception e) {
                            System.out.println("Ошибка при добавлении клиента: " + e.getMessage());
                        }
                        break;

                    case 2:
                        System.out.println("=== СПИСОК КЛИЕНТОВ ===");
                        List<Client> allClients = bank.getAllClients();

                        if (allClients.isEmpty()) {
                            System.out.println("Клиентов нет");
                        } else {
                            for (Client client : allClients) {
                                System.out.println(client.getClientInfo());
                                System.out.println("----------------------");
                            }
                        }
                        break;

                    case 3:

                        try {
                            System.out.print("Введите номер паспорта клиента: ");
                            String pass = scanner.nextLine();

                            Client foundClient = bank.findClientByPassport(pass);
                            if (foundClient == null) {
                                System.out.println("Клиент с таким паспортом не найден!");
                                break;
                            }


                            double clientCurrentAmount = foundClient.getDeposit().getInitialAmount();


                            java.text.DecimalFormat df = new java.text.DecimalFormat("#");


                            double am;
                            while (true) {
                                System.out.print("Введите сумму пополнения: ");
                                String amInput = scanner.nextLine().trim();


                                if (amInput.endsWith(",") || amInput.contains(",,") || amInput.endsWith(".") || amInput.contains("..")) {
                                    System.out.println("Ошибка! После запятой должна быть десятичная часть. Попробуйте снова.");
                                    continue;
                                }


                                amInput = amInput.replace(',', '.');

                                try {
                                    am = Double.parseDouble(amInput);
                                    if (am > 0 && am <= 1_000_000_000) {

                                        if (clientCurrentAmount + am <= 1_000_000_000) {
                                            break;
                                        } else {
                                            System.out.println("Ошибка! Итоговая сумма вклада не может превышать 1 000 000 000.");
                                            System.out.println("Текущая сумма: " + df.format(clientCurrentAmount) +
                                                    ", можно добавить не более: " + df.format(1_000_000_000 - clientCurrentAmount));
                                        }
                                    } else if (am <= 0) {
                                        System.out.println("Ошибка! Сумма должна быть положительной. Попробуйте снова.");
                                    } else {
                                        System.out.println("Ошибка! Сумма пополнения не может превышать 1 000 000 000. Попробуйте снова.");
                                    }
                                } catch (NumberFormatException e) {
                                    System.out.println("Ошибка! Введите корректное число (например: 500 или 750.50).");
                                }
                            }

                            bank.topUpDeposit(pass, am);
                            System.out.println("Успешно! Текущая сумма: " + df.format(foundClient.getDeposit().getInitialAmount()));

                        } catch (Exception e) {
                            System.out.println("Ошибка при пополнении вклада: " + e.getMessage());
                        }
                        break;
                    case 4:

                        double totalPayouts = bank.calculateTotalPayouts();
                        System.out.println("Общая сумма выплат по процентам: " + totalPayouts + " руб.");
                        break;

                    case 5:
                        System.out.println("Выход из программы...");
                        scanner.close();
                        bank.clearResources();
                        return;


                    default:
                        System.out.println("Неверный выбор! Введите число от 1 до 5.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Ошибка! Введите цифру от 1 до 5.");
                scanner.nextLine();
            } catch (Exception e) {
                System.out.println("Произошла ошибка: " + e.getMessage());
                scanner.nextLine();
            }

            System.out.println();
        }
    }
}



