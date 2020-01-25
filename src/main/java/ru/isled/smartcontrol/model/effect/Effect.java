package ru.isled.smartcontrol.model.effect;

import javafx.beans.property.IntegerProperty;
import ru.isled.smartcontrol.view.*;

import java.util.List;

public enum Effect {
    Разгорание {
        @Override
        public void apply(List<IntegerProperty> values, Integer cols, Integer rows) {
            FadeInEffectController effect = FadeInEffectController.get(values, cols, rows);
            effect.apply();
        }
    },

    Угасание {
        @Override
        public void apply(List<IntegerProperty> values, Integer cols, Integer rows) {
            FadeOutEffectController effect = FadeOutEffectController.get(values, cols, rows);
            effect.apply();
        }
    },

    Случайно {
        @Override
        public void apply(List<IntegerProperty> values, Integer cols, Integer rows) {
            ChaosEffectController effect = ChaosEffectController.get(values, cols, rows);
            effect.apply();
        }
    },

    Блик {
        @Override
        public void apply(List<IntegerProperty> values, Integer cols, Integer rows) {
            GlareEffectController effect = GlareEffectController.get(values, cols, rows);
            effect.apply();
        }
    },

    Сборка {
        @Override
        public void apply(List<IntegerProperty> values, Integer cols, Integer rows) {
            BuildingEffectController effect = BuildingEffectController.get(values, cols, rows);
            effect.apply();
        }
    },

    Наполнение {
        @Override
        public void apply(List<IntegerProperty> values, Integer cols, Integer rows) {
            FillingEffectController effect = FillingEffectController.get(values, cols, rows);
            effect.apply();
        }
    };

    public abstract void apply(List<IntegerProperty> values,
                               Integer cols, Integer rows);
}
