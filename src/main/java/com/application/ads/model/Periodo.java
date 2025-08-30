    package com.application.ads.model;

    import jakarta.persistence.*;
    import jakarta.validation.constraints.NotBlank;
    import java.util.List;

    @Entity
    public class Periodo {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @NotBlank
        private String nome;

        @ElementCollection
        @CollectionTable(name = "disciplinas", joinColumns = @JoinColumn(name = "periodo_id"))
        @Column(name = "disciplina")
        private List<String> disciplinas;

        // Construtores
        public Periodo() {}
        public Periodo(String nome, List<String> disciplinas) {
            this.nome = nome;
            this.disciplinas = disciplinas;
        }

        // Getters e Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }
        public List<String> getDisciplinas() { return disciplinas; }
        public void setDisciplinas(List<String> disciplinas) { this.disciplinas = disciplinas; }
    }