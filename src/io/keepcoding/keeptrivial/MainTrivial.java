package io.keepcoding.keeptrivial;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class MainTrivial {
	// Nos creamos un mapa clave-valor, con los temas como clave, y una lista de Questions con las preguntas de ese tema
	// Se rellenará cuando inicializamos los topic
	private static Map<String, List<Question>> questionsMap = new HashMap<>();

	public static void main(String[] args) {

		// iniciamos los equipos
		List<Team> teams = new ArrayList<>();
		List<String> topicNames = getQuestions();

		// Hemos
		try (Scanner scanner = new Scanner(System.in)) {
			System.out.println("Dime qué nombre quieres para el primer equipo:");

			while (true) {
				String input = scanner.nextLine();
				if (input.equalsIgnoreCase("Q")) {
					if (teams.isEmpty()) {
						System.out.println("Debes introducir al menos un equipo para comenzar el juego.");
						continue;
					} else {
						break;
					}
				}
				teams.add(new Team(input, topicNames));
				System.out.println(
						"Dime qué nombre quieres para el siguiente equipo, o escribe \"Q\" para empezar el juego:");
			}

			// Iniciamos el juego
			boolean exit = false;
			Random random = new Random();
			while (!exit) {
				for (Team team : teams) {
					System.out.println("Turno del equipo: " + team.getName());

					// Mostrar puntuaciones actuales
					printHashtagLine(8);
					System.out.println("Puntuaciones actuales:");
					printHashtagLine(4);
					for (Team t : teams) {
						System.out.println(t.getName() + ": " + t.getPoints() + " puntos");
					}
					printHashtagLine(8);

					List<String> remainingTopics = new ArrayList<>();
					for (String topic : topicNames) {
						if (!team.getTopics().get(topic)) {
							remainingTopics.add(topic);
						}
					}

					if (remainingTopics.isEmpty()) {
						exit = true;
						title("Ha ganado: " + team.getName());
						break;
					}

					String topicName = remainingTopics.get(random.nextInt(remainingTopics.size()));
					Question question = getRandomQuestion(topicName);

					if (question != null) {
						System.out.println("Pregunta de " + topicName + ": " + question.getQuestion());
						System.out.println("1. " + question.getAnswer1());
						System.out.println("2. " + question.getAnswer2());
						System.out.println("3. " + question.getAnswer3());
						System.out.println("4. " + question.getAnswer4());

						int userAnswer;
						while (true) {
							if (scanner.hasNextInt()) {
								userAnswer = scanner.nextInt();
								scanner.nextLine(); // consume the newline
								if (userAnswer >= 1 && userAnswer <= 4) {
									break;
								}
							} else {
								scanner.nextLine(); // consume the invalid input
							}
							// Si la respuesta del usuario llega hasta aquí es que no era un número entre 1
							// y 4, debe ser marcada como incorrecta
							userAnswer = -1;
							System.out.println("Respuesta no válida, debemos marcar tu respuesta como incorrecta.");
							break;
						}

						if (userAnswer == question.getRightOption()) {
							System.out.println("¡Correcto!");
							team.setTopicCompleted(topicName);
						} else {
							// No mostramos cuál era la respuesta correcta para no dar facilidades en próximas preguntas
							// pero esta línea se puede activar para funciones de test de la aplicación
							// System.out.println("Incorrecto. La respuesta correcta era: " + getCorrectAnswer(question));
							System.out.println("Respuesta incorrecta.");
						}
					}

					if (team.hasWon()) {
						exit = true;
						title("Ha ganado: " + team.getName());
						break;
					}
				}
			}
		}
	}

	public static void title(String text) {
		int length = text.length();
		printHashtagLine(length + 4); // Bordes

		System.out.println("# " + text + " #");

		printHashtagLine(length + 4);
	}

	public static void printHashtagLine(int length) {
		for (int i = 0; i < length; i++) {
			System.out.print("#");
		}
		System.out.println();
	}

	public static boolean esTransformableAEntero(String cadena) {
		try {
			Integer.parseInt(cadena);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	private static int getRandomInt(int max) {
		return new Random().nextInt(max);
	}

	private static List<String> getQuestions() {
		List<String> list = new ArrayList<>();

		File folder = new File("questions");
		if (!folder.exists()) {
			title("Error al cargar el fichero");
		} else {
			File[] filesList = folder.listFiles();

			for (File file : filesList) {
				if (file.isFile() && file.getName().endsWith(".txt")) {
					var topicName = file.getName().substring(0, file.getName().length() - 4);

					// TODO create topic (Hecho)
					list.add(topicName);
					
					// READ the question
					// Inicializamos la lista de Questions que alojaremos en el mapa topic-questions
					List<Question> questions = new ArrayList<>();
					try (BufferedReader br = new BufferedReader(new FileReader(file))) {
						String line;
						List<String> block = new ArrayList<>();
						while ((line = br.readLine()) != null) {
							block.add(line);
							if (block.size() == 6) { // número de lineas que componen una pregunta
								var question = block.get(0);
								var answer1 = block.get(1);
								var answer2 = block.get(2);
								var answer3 = block.get(3);
								var answer4 = block.get(4);
								int rightOption = Integer.parseInt(block.get(5));
								
								// TODO create question (Hecho)
								questions.add(new Question(question, answer1, answer2, answer3, answer4, rightOption,
										topicName));
								block.clear();
							}
						}
						// TODO Add to list (Hecho) 
						/* solo que yo no he creado una lista de preguntas, he creado un mapa, 
						 * y dentro del mapa está la lista de preguntas, así tengo identificado el topic por la clave del mapa
						 * y el valor es la lista de questions
						 */
						questionsMap.put(topicName, questions);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return list;
	}

	private static Question getRandomQuestion(String topicName) {
		List<Question> questions = questionsMap.get(topicName);
		if (questions != null && !questions.isEmpty()) {
			int index = getRandomInt(questions.size());
			return questions.get(index);
		}
		return null;
	}

}