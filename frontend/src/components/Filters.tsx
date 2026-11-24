import { useState, useEffect } from "react";
import { Ingredient, Tag } from "../types/Tags";
import { useSearchParams } from "react-router-dom";
import { AutocompleteInput } from "./AutocompleteInput";
import { Navigation } from "./Navigation";

interface FiltersProps {
    forDiets: boolean
    onApply: () => void;
}

export function Filters({ forDiets, onApply } : FiltersProps) {
    const [searchParams, setSearchParams] = useSearchParams();

    const [allIngs, setAllIngs] = useState<Ingredient[]>([]);
    const [allTags, setAllTags] = useState<Tag[]>([]);
    
    useEffect(() => {
        fetch("/backend/search")
            .then((res) => res.json())
            .then((data) => {
                setAllIngs(data.ingredients);
                setAllTags(data.tags)
            });
    }, []);

    const handleClearAll = () => {
        setSearchParams({});
    };

    const handleSubmit = () => {
        if (forDiets && searchParams.get("days_count") === null) {
            setSearchParams((prevParams) => {
                prevParams.set("days_count", "1");
                return prevParams;
            })
        }
        onApply();
    }

    return (
        <div className="search-container">
        <header className="search-header">
            <h1>Фильтр рецептов</h1>
        </header>

        {forDiets && (
            <label>
                Выберите количество дней
                <select
                    onChange={(e) => setSearchParams((prevParams) => {
                            prevParams.set("days_count", e.target.value);
                            return prevParams;
                    })}
                >
                    <option value="1">1 день</option>
                    <option value="3">3 дня</option>
                    <option value="7">Неделя</option>
                </select>
            </label>
        )}

        <AutocompleteInput
            paramName="exclude_ingredients"
            allElemsList={allIngs}
            sectionTitle="Black List ингредиентов"
            sectionDescription="Ингредиенты, которые НЕ должны быть в рецепте"
            inputPlaceholder="Введите ингредиент и нажмите ','"
            emptyMessage="Нет запрещённых ингредиентов"
            isBlackList={true} />

        <AutocompleteInput
            paramName="include_ingredients"
            allElemsList={allIngs}
            sectionTitle="White List ингредиентов"
            sectionDescription="Обязательные ингредиенты"
            inputPlaceholder="Введите ингредиент и нажмите ','"
            emptyMessage="Нет обязательных ингредиентов"
            isBlackList={false} />
            
        <AutocompleteInput
            paramName="exclude_tags"
            allElemsList={allTags}
            sectionTitle="Black List тегов"
            sectionDescription="Теги, которыми НЕ должен быть отмечен рецепт"
            inputPlaceholder="Введите тег и нажмите ','"
            emptyMessage="Нет запрещённых тегов"
            isBlackList={true} />
            
        <AutocompleteInput
            paramName="include_tags"
            allElemsList={allTags}
            sectionTitle="White List тегов"
            sectionDescription="Обязательные теги"
            inputPlaceholder="Введите тег и нажмите ','"
            emptyMessage="Нет обязательных тегов"
            isBlackList={false} />
        
        <div className="action-buttons">
            <button className="btn-clear" onClick={handleClearAll}>
            🗑️ Очистить всё
            </button>
            
            <button className="btn-search" onClick={handleSubmit}>
            🔍 Поиск рецептов
            </button>
        </div>

        <Navigation />
        </div>
    );
}