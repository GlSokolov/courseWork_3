package com.example.coursework_3.model;

import lombok.*;

import javax.validation.constraints.*;

@Getter
@EqualsAndHashCode
@NoArgsConstructor
@ToString
@Setter
public class Socks {

    @NotBlank
    @NotEmpty
    private Size size;
    private Color color;
    @Positive
    @Max(value = 100)
    @Min(0)
    private int cottonPart;
    @Positive
    @Min(1)
    private int quantity;

    public enum Size {
        S("35-38"), M("39-42"), L("43-46"), XL("47-49");

        private final String translate;

        Size(String translate) {
            this.translate = translate;
        }

        public String getRussianSize() {
            return translate;
        }
    }
    public enum Color {
        WHITE("Белый"), BLACK("Черный"), RED("Красный"), YELLOW("Желтый"), BLUE("Синий");

        private final String translate;

        Color(String translate) {
            this.translate = translate;
        }

        public String getNameColor() {
            return translate;
        }
    }
}
