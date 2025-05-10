package org.example;

import java.util.*;
import java.util.stream.Collectors;

public class Game {
    public static void main(String[] args) {
        System.out.println("Счетчик очков для Гвинта");
        Scanner scanner = new Scanner(System.in);

        Squads melee = new Squads(scanner);
        Squads longRange = new Squads(scanner);
        Squads siege = new Squads(scanner);

        int heroSum = handleHeroCards(scanner);
        System.out.println("Сумма: " + heroSum);

        processSquad("Рукопашные отряды:", melee);
        processSquad("Дальнобойные отряды", longRange);
        processSquad("Осадные отряды", siege);

        int total = melee.getSum() + longRange.getSum() + siege.getSum() + heroSum;
        System.out.println("Общий результат: " + total);

        scanner.close();
    }

    private static void processSquad(String title, Squads squad) {
        System.out.println(title);
        squad.process();
    }

    static String getYesNoAnswer(Scanner scanner) {
        while (true) {
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("да") || input.equals("нет")) return input;
            System.out.println("Ошибка! Введите 'да' или 'нет':");
        }
    }

    private static int handleHeroCards(Scanner scanner) {
        System.out.println("Есть ли у вас на поле специальные карты - да/нет");
        List<Integer> cards = new ArrayList<>();

        while (true) {
            String answer = getYesNoAnswer(scanner);
            if (answer.equals("да")) {
                System.out.println("Перечислите силу этих карт:");
                cards.addAll(readNumbers(scanner, "Ошибка: %s - не является силой"));
                return cards.stream().mapToInt(Integer::intValue).sum();
            }
            if (answer.equals("нет")) return 0;
            System.out.println("Ошибка! Допустимы только 'да' или 'нет'");
        }
    }

    static List<Integer> readNumbers(Scanner scanner, String errorMessage) {
        while (true) {
            try {
                String inputLine = scanner.nextLine();
                return Arrays.stream(inputLine.split("\\s+"))
                        .map(Integer::parseInt)
                        .collect(Collectors.toCollection(ArrayList::new));
            } catch (NumberFormatException e) {
                String invalidInput = e.getMessage().split(": ")[1].replaceAll("\"", "");
                System.out.println(String.format(errorMessage, invalidInput));
            }
        }
    }
}

class Squads {

    private final Scanner scanner;
    private int total;
    private List<Integer> originalCards;
    private List<Integer> modifiedCards;

    public Squads(Scanner scanner) {
        this.scanner = scanner;
    }

    public void process() {
        originalCards = readRegularCards();
        boolean hasWeather = checkWeather();
        modifiedCards = new ArrayList<>(originalCards);

        applyWeatherEffect(hasWeather);
        handleStrongConnection();
        applySurgeOfStrength();
        int baseSum = calculateBaseSum();
        applyCommanderHorn(baseSum);
    }

    private List<Integer> readRegularCards() {
        System.out.println("Перечислите силу обычных карт:");
        return Game.readNumbers(scanner, "Ошибка: %s - не является силой");
    }

    private boolean checkWeather() {
        System.out.println("Открыта погодная карта - да/нет");
        return Game.getYesNoAnswer(scanner).equals("да");
    }

    private void applyWeatherEffect(boolean hasWeather) {
        if (hasWeather) {
            modifiedCards.replaceAll(v -> 1);
        }

        System.out.println(modifiedCards);
    }

    private void handleStrongConnection() {
        System.out.println("Есть ли карты 'Прочная связь (*2)' - да/нет");
        if (Game.getYesNoAnswer(scanner).equals("да")) {
            boolean validInput = false;
            Set<Integer> surgeValues = new HashSet<>();

            while (!validInput) {
                System.out.println("Перечислите их силу:");
                List<Integer> input = Game.readNumbers(scanner, "Ошибка: %s - не является силой");
                boolean allValid = true;
                for (int value : input) {
                    if (!originalCards.contains(value)) {
                        System.out.println("Данных карты не было в списке обычный карт");
                        allValid = false;
                    }
                }
                if (allValid) {
                    surgeValues.addAll(input);
                    validInput = true;
                }
            }
            for (int originalValue : surgeValues) {

                for (int i = 0; i < originalCards.size(); i++) {
                    if (originalCards.get(i) == originalValue) {
                        modifiedCards.set(i, modifiedCards.get(i) * 2);
                    }
                }
            }
        }
        System.out.println(modifiedCards);
    }

    private void applySurgeOfStrength() {
        System.out.println("Есть ли карты 'Прилив сил (+1)' - да/нет");
        if (Game.getYesNoAnswer(scanner).equals("да")) {
            int surgeValue = 0;
            boolean validInput = false;

            while (!validInput) {
                System.out.println("Сила этой карты:");
                List<Integer> input = Game.readNumbers(scanner, "Ошибка: %s - не является силой");
                surgeValue = input.get(0);
                if (!originalCards.contains(surgeValue)) {
                    System.out.println("Ошибка: Карты с силой " + surgeValue + " нет в списке!");
                    continue;
                }
                validInput = true;
            }

            int surgeIndex = originalCards.indexOf(surgeValue);
            for (int i = 0; i < originalCards.size(); i++) {
                if (i != surgeIndex) {
                    modifiedCards.set(i, modifiedCards.get(i) + 1);
                }
            }
        }
        System.out.println(modifiedCards);
    }

    private int calculateBaseSum() {
        return modifiedCards.stream().mapToInt(Integer::intValue).sum();
    }

    private void applyCommanderHorn(int baseSum) {
        System.out.println("Применена карта 'Командирский рог' - да/нет");
        boolean useHorn = Game.getYesNoAnswer(scanner).equals("да");

        total = baseSum * (useHorn ? 2 : 1);
        System.out.println("Сумма: " + total);
    }

    public int getSum() {
        return total;
    }
}
