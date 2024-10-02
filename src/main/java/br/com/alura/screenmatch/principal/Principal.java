package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.service.model.Episodio;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.model.ConverteDados;
import br.com.alura.screenmatch.service.model.DadosEpisodio;
import br.com.alura.screenmatch.service.model.DadosSerie;
import br.com.alura.screenmatch.service.model.DadosTemporada;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {

    private ConsumoApi consumoApi = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();
    private Scanner leitura = new Scanner(System.in);
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=c5f423d6";

    public void exibeMenu(){
        System.out.println("Digite o nome da série para buscar");
        String nomeSerie = leitura.nextLine();

        String json = consumoApi.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        System.out.println(dados);

        List<DadosTemporada> temporadas = new ArrayList<>();

        for (int i = 1; i <= dados.totalTemporadas(); i++){
            json = consumoApi.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + "&season=" + i + API_KEY);
            DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
            temporadas.add(dadosTemporada);
        }
        temporadas.forEach(System.out::println);
//
//        for (int i = 0; i < dados.totalTemporadas(); i++){
//            List <DadosEpisodio> episodiosTemporada = temporadas.get(i).episodios();
//            for (int j = 0; j < episodiosTemporada.size(); j++){
//                System.out.println(episodiosTemporada.get(j).titulo());
//            }
//        }

        //lambdas

        //temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));


/*        List<String> nomes = Arrays.asList("Lucas", "Cássio","Marbi", "Beto", "Beti");

        nomes.stream()
                .sorted()
                .limit(3)
                .filter(n -> n.startsWith("C"))
                .map(n -> n.toUpperCase())
                .forEach(System.out::println);*/


        List<DadosEpisodio> dadosEpisodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream())
                .collect(Collectors.toList());

        dadosEpisodios.stream()
                .filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
                .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())
                .limit(5)
                .forEach(System.out::println);

        List<Episodio> episodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream()
                        .map(d -> new Episodio(t.numero(), d))
                ).collect(Collectors.toList());

        episodios.forEach(System.out::println);
    }
}
