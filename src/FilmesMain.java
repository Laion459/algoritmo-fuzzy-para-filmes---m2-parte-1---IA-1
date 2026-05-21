import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class FilmesMain {
	public static void main(String[] args) {

		// orcamento em milhoes de dolares
		VariavelFuzzy muitoBarato = new VariavelFuzzy("Muito Barato", 0, 0, 8, 20);
		VariavelFuzzy barato = new VariavelFuzzy("Barato", 8, 18, 30, 55);
		VariavelFuzzy cmedio = new VariavelFuzzy("Custo Medio", 25, 45, 65, 95);
		VariavelFuzzy caro = new VariavelFuzzy("Caro", 55, 85, 120, 180);
		VariavelFuzzy muitocaro = new VariavelFuzzy("Muito Caro", 100, 150, 400, 400);

		GrupoVariaveis grupoOrcamento = new GrupoVariaveis();
		grupoOrcamento.add(muitoBarato);
		grupoOrcamento.add(barato);
		grupoOrcamento.add(cmedio);
		grupoOrcamento.add(caro);
		grupoOrcamento.add(muitocaro);

		// vote_average do csv movies/filmes (0 a 10)
		GrupoVariaveis grupoRating = new GrupoVariaveis();
		grupoRating.add(new VariavelFuzzy("MR", 0, 0, 4.5f, 5.5f));
		grupoRating.add(new VariavelFuzzy("R", 4.0f, 5.5f, 6.0f, 7.0f));
		grupoRating.add(new VariavelFuzzy("B", 6.0f, 6.5f, 7.5f, 8.5f));
		grupoRating.add(new VariavelFuzzy("MB", 7.5f, 8.5f, 10, 10));

		GrupoVariaveis grupoVotos = new GrupoVariaveis();
		grupoVotos.add(new VariavelFuzzy("V_MPV", 0, 0, 50, 150));
		grupoVotos.add(new VariavelFuzzy("V_PV", 80, 150, 400, 700));
		grupoVotos.add(new VariavelFuzzy("V_MEV", 350, 700, 1500, 2500));
		grupoVotos.add(new VariavelFuzzy("V_BAV", 1500, 2500, 4000, 7000));
		grupoVotos.add(new VariavelFuzzy("V_MUV", 3500, 5500, 14000, 14000));

		// media das notas dos generos do filme
		GrupoVariaveis grupoNotaGenero = new GrupoVariaveis();
		grupoNotaGenero.add(new VariavelFuzzy("G_R", 0, 0, 4.5f, 6.0f));
		grupoNotaGenero.add(new VariavelFuzzy("G_M", 5.0f, 6.0f, 7.0f, 8.0f));
		grupoNotaGenero.add(new VariavelFuzzy("G_B", 7.5f, 8.5f, 10, 10));

		// saida: atratividade do filme (NA, A, MA)
		GrupoVariaveis grupoAtratividade = new GrupoVariaveis();
		grupoAtratividade.add(new VariavelFuzzy("NA", 0, 0, 3, 6));
		grupoAtratividade.add(new VariavelFuzzy("A", 5, 7, 8, 10));
		grupoAtratividade.add(new VariavelFuzzy("MA", 7, 9, 10, 10));

		try {
			BufferedReader bfr = new BufferedReader(new FileReader(new File("filmes_filtrados.csv")));

			bfr.readLine();

			String line = "";

			GenerosNota generosNota = new GenerosNota();
			ArrayList<ResultadoFilme> resultados = new ArrayList<>();

			imprimeCabecalhoTabela();

			while ((line = bfr.readLine()) != null) {
				String spl[] = line.split(";");
				HashMap<String, Float> asVariaveis = new HashMap<String, Float>();

				float orcamento = Float.parseFloat(spl[3]);
				grupoOrcamento.fuzzifica(orcamento, asVariaveis);

				float rating = Float.parseFloat(spl[4]);
				grupoRating.fuzzifica(rating, asVariaveis);

				float votos = Float.parseFloat(spl[5]);
				grupoVotos.fuzzifica(votos, asVariaveis);

				// generos separados por espaco
				String generos[] = spl[2].split(" ");
				float notagenero = 0;
				int numeroNotas = 0;
				for (int i = 0; i < generos.length; i++) {
					Float anota = generosNota.notasGeneros.get(generos[i].toLowerCase().trim());
					if (anota != null) {
						notagenero += anota;
						numeroNotas++;
					}
				}
				float notafinalgenero = 0;
				if (numeroNotas > 0) {
					notafinalgenero = notagenero / numeroNotas;
				}
				grupoNotaGenero.fuzzifica(notafinalgenero, asVariaveis);

				// comeca zerado; as regras vao preenchendo
				asVariaveis.put("NA", 0f);
				asVariaveis.put("A", 0f);
				asVariaveis.put("MA", 0f);

				// regras iguais as da aula (orcamento + rating + votos + genero)
				rodaRegraE(asVariaveis, "Barato", "B", "A");
				rodaRegraE(asVariaveis, "Muito Barato", "B", "A");
				rodaRegraE(asVariaveis, "Muito Barato", "MB", "MA");
				rodaRegraE(asVariaveis, "Barato", "MB", "MA");
				rodaRegraE(asVariaveis, "Barato", "R", "NA");
				rodaRegraE(asVariaveis, "Muito Barato", "R", "A");
				rodaRegraE(asVariaveis, "Muito Barato", "MR", "NA");
				rodaRegraE(asVariaveis, "Muito Caro", "MR", "NA");
				rodaRegraE(asVariaveis, "Muito Caro", "R", "NA");
				rodaRegraE(asVariaveis, "Muito Caro", "B", "NA");
				rodaRegraE(asVariaveis, "Muito Caro", "MB", "A");

				rodaRegraE(asVariaveis, "MA", "V_MPV", "NA");
				rodaRegraE(asVariaveis, "MA", "V_PV", "A");
				rodaRegraE(asVariaveis, "MA", "V_MEV", "A");

				rodaRegraE(asVariaveis, "A", "V_MPV", "NA");
				rodaRegraE(asVariaveis, "A", "V_PV", "NA");
				rodaRegraE(asVariaveis, "A", "V_MEV", "NA");

				rodaRegraE(asVariaveis, "G_B", "Barato", "MA");
				rodaRegraE(asVariaveis, "G_M", "Barato", "A");
				rodaRegraE(asVariaveis, "G_R", "Barato", "NA");

				rodaRegraE(asVariaveis, "G_B", "Muito Barato", "MA");
				rodaRegraE(asVariaveis, "G_M", "Muito Barato", "MA");
				rodaRegraE(asVariaveis, "G_R", "Muito Barato", "NA");

				rodaRegraE(asVariaveis, "G_B", "Caro", "MA");
				rodaRegraE(asVariaveis, "G_M", "Caro", "NA");
				rodaRegraE(asVariaveis, "G_R", "Caro", "NA");

				rodaRegraE(asVariaveis, "G_B", "Muito Caro", "A");
				rodaRegraE(asVariaveis, "G_M", "Muito Caro", "NA");
				rodaRegraE(asVariaveis, "G_R", "Muito Caro", "NA");

				float NA = asVariaveis.get("NA");
				float A = asVariaveis.get("A");
				float MA = asVariaveis.get("MA");

				// defuzzificacao por media ponderada
				float score = (NA * 1.5f + A * 7.0f + MA * 9.5f) / (NA + A + MA);

				imprimeLinhaFilme(spl[1], spl[2], orcamento, rating, votos, NA, A, MA, score);
				resultados.add(new ResultadoFilme(spl[1], spl[2], orcamento, rating, votos, NA, A, MA, score));
			}

			imprimeBordaTabela();
			imprimeTop20Melhores(resultados);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static final int W_RANK = 4;
	private static final int W_TITLE = 32;
	private static final int W_GENRES = 28;
	private static final int W_BUDGET = 8;
	private static final int W_VOTE = 6;
	private static final int W_COUNT = 10;
	private static final int W_FUZZY = 6;
	private static final int W_SCORE = 8;

	private static void imprimeCabecalhoTabela() {
		imprimeBordaTabela();
		System.out.printf(
				"| %-" + W_TITLE + "s | %-" + W_GENRES + "s | %" + W_BUDGET + "s | %" + W_VOTE + "s | %" + W_COUNT
						+ "s | %" + W_FUZZY + "s | %" + W_FUZZY + "s | %" + W_FUZZY + "s | %" + W_SCORE + "s |%n",
				"TITLE", "GENRES", "BUDGET", "VOTE", "COUNT", "NA", "A", "MA", "SCORE");
		imprimeBordaTabela();
	}

	private static void imprimeLinhaFilme(String titulo, String generos, float orcamento, float rating,
			float votos, float na, float a, float ma, float score) {
		System.out.printf(
				"| %-" + W_TITLE + "s | %-" + W_GENRES + "s | %" + W_BUDGET + ".1f | %" + W_VOTE + ".1f | %" + W_COUNT
						+ ".0f | %" + W_FUZZY + ".2f | %" + W_FUZZY + ".2f | %" + W_FUZZY + ".2f | %" + W_SCORE
						+ ".2f |%n",
				truncar(titulo, W_TITLE),
				truncar(generos, W_GENRES),
				orcamento,
				rating,
				votos,
				na,
				a,
				ma,
				score);
	}

	private static void imprimeBordaTabela() {
		System.out.println(
				"+" + "-".repeat(W_TITLE + 2)
						+ "+" + "-".repeat(W_GENRES + 2)
						+ "+" + "-".repeat(W_BUDGET + 2)
						+ "+" + "-".repeat(W_VOTE + 2)
						+ "+" + "-".repeat(W_COUNT + 2)
						+ "+" + "-".repeat(W_FUZZY + 2)
						+ "+" + "-".repeat(W_FUZZY + 2)
						+ "+" + "-".repeat(W_FUZZY + 2)
						+ "+" + "-".repeat(W_SCORE + 2)
						+ "+");
	}

	// Top 20 melhores
	private static void imprimeTop20Melhores(ArrayList<ResultadoFilme> resultados) {
		Collections.sort(resultados, Comparator.comparing(ResultadoFilme::getScore).reversed());

		System.out.println();
		System.out.println("=== TOP 20 MELHORES CLASSIFICACOES (maior SCORE) ===");
		imprimeCabecalhoTabelaTop();

		int limite = Math.min(20, resultados.size());
		for (int i = 0; i < limite; i++) {
			ResultadoFilme filme = resultados.get(i);
			imprimeLinhaFilmeTop(
					i + 1,
					filme.titulo,
					filme.generos,
					filme.orcamento,
					filme.rating,
					filme.votos,
					filme.na,
					filme.a,
					filme.ma,
					filme.score);
		}
		imprimeBordaTabelaTop();
	}

	private static void imprimeCabecalhoTabelaTop() {
		imprimeBordaTabelaTop();
		System.out.printf(
				"| %" + W_RANK + "s | %-" + W_TITLE + "s | %-" + W_GENRES + "s | %" + W_BUDGET + "s | %" + W_VOTE
						+ "s | %" + W_COUNT + "s | %" + W_FUZZY + "s | %" + W_FUZZY + "s | %" + W_FUZZY + "s | %"
						+ W_SCORE + "s |%n",
				"#", "TITLE", "GENRES", "BUDGET", "VOTE", "COUNT", "NA", "A", "MA", "SCORE");
		imprimeBordaTabelaTop();
	}

	private static void imprimeLinhaFilmeTop(int posicao, String titulo, String generos, float orcamento,
			float rating, float votos, float na, float a, float ma, float score) {
		System.out.printf(
				"| %" + W_RANK + "d | %-" + W_TITLE + "s | %-" + W_GENRES + "s | %" + W_BUDGET + ".1f | %" + W_VOTE
						+ ".1f | %" + W_COUNT + ".0f | %" + W_FUZZY + ".2f | %" + W_FUZZY + ".2f | %" + W_FUZZY
						+ ".2f | %" + W_SCORE + ".2f |%n",
				posicao,
				truncar(titulo, W_TITLE),
				truncar(generos, W_GENRES),
				orcamento,
				rating,
				votos,
				na,
				a,
				ma,
				score);
	}

	private static void imprimeBordaTabelaTop() {
		System.out.println(
				"+" + "-".repeat(W_RANK + 2)
						+ "+" + "-".repeat(W_TITLE + 2)
						+ "+" + "-".repeat(W_GENRES + 2)
						+ "+" + "-".repeat(W_BUDGET + 2)
						+ "+" + "-".repeat(W_VOTE + 2)
						+ "+" + "-".repeat(W_COUNT + 2)
						+ "+" + "-".repeat(W_FUZZY + 2)
						+ "+" + "-".repeat(W_FUZZY + 2)
						+ "+" + "-".repeat(W_FUZZY + 2)
						+ "+" + "-".repeat(W_SCORE + 2)
						+ "+");
	}

	private static String truncar(String texto, int larguraMaxima) {
		if (texto == null) {
			return "";
		}
		if (texto.length() <= larguraMaxima) {
			return texto;
		}
		if (larguraMaxima <= 3) {
			return texto.substring(0, larguraMaxima);
		}
		return texto.substring(0, larguraMaxima - 3) + "...";
	}

	private static class ResultadoFilme {
		String titulo;
		String generos;
		float orcamento;
		float rating;
		float votos;
		float na;
		float a;
		float ma;
		float score;

		ResultadoFilme(String titulo, String generos, float orcamento, float rating, float votos,
				float na, float a, float ma, float score) {
			this.titulo = titulo;
			this.generos = generos;
			this.orcamento = orcamento;
			this.rating = rating;
			this.votos = votos;
			this.na = na;
			this.a = a;
			this.ma = ma;
			this.score = score;
		}

		float getScore() {
			return score;
		}
	}

	private static void rodaRegraE(HashMap<String, Float> asVariaveis, String var1, String var2, String varr) {
		float v = Math.min(asVariaveis.get(var1), asVariaveis.get(var2));
		if (asVariaveis.keySet().contains(varr)) {
			float vatual = asVariaveis.get(varr);
			asVariaveis.put(varr, Math.max(vatual, v));
		} else {
			asVariaveis.put(varr, v);
		}
	}

	private static void rodaRegraOU(HashMap<String, Float> asVariaveis, String var1, String var2, String varr) {
		float v = Math.max(asVariaveis.get(var1), asVariaveis.get(var2));
		if (asVariaveis.keySet().contains(varr)) {
			float vatual = asVariaveis.get(varr);
			asVariaveis.put(varr, Math.max(vatual, v));
		} else {
			asVariaveis.put(varr, v);
		}
	}
}
