import { useState, useEffect } from "react";
import { useSearchParams } from "react-router-dom";

interface Elem {
    id: number;
    title: string;
}

interface AutocompleteInputProps {
    paramName: string;
    allElemsList: Elem[];
    sectionTitle: string;
    sectionDescription: string;
    inputPlaceholder: string;
    emptyMessage: string;
    isBlackList: boolean;
}

export function AutocompleteInput({ paramName, allElemsList,
    sectionTitle, sectionDescription,
    inputPlaceholder, emptyMessage,
    isBlackList } : AutocompleteInputProps) {
    const [searchParams, setSearchParams] = useSearchParams();
    const [openHint, setOpenHint] = useState(false);

    const [elemsListInput, setElemsListInput] = useState<string>('');

    const [elemsList, setElemsList] = useState<Elem[]>(() => {
        const elems = searchParams.get(paramName);
        if (elems) {
            const ids: number[] = elems.split(',').map((str) => str.trim())
                .filter((str) => str !== '').map(Number).filter((id) => !Number.isNaN(id));
            return allElemsList.filter((elem) => ids.includes(elem.id));
        }
        return [];
    });

    useEffect(() => {
        setSearchParams((prevParams) => {
            if (elemsList.length === 0) {
                prevParams.delete(paramName);
            } else {
                prevParams.set(paramName, elemsList.map((elem) => elem.id).join(','));
            }
            return prevParams;
        }); 
    }, [elemsList, setSearchParams]);

    const filteredSuggestions = allElemsList.filter((elem) =>
        elem.title.toLowerCase().includes(elemsListInput.toLowerCase())).slice(0, 5);

    const handleElemsListAdd = (e: React.KeyboardEvent<HTMLInputElement>) => {
        if (e.key === 'Enter' || e.key === ',') {
            e.preventDefault();
            const elemTitle = elemsListInput.trim().toLowerCase();
            const elemValue = allElemsList.find((elem) => elem.title === elemTitle);
            if (elemValue && !elemsList.includes(elemValue)) {
                setElemsList([...elemsList, elemValue]);
                setOpenHint(false);
            }
            setElemsListInput('');
        }
    };

    const handleElemsListRemove = (elemToRemove: Elem) => {
        setElemsList(elemsList.filter(elem => elem !== elemToRemove));
    };

    const handleSelect = (elem: Elem) => {
        if (elem && !elemsList.includes(elem)) {
            setElemsList([...elemsList, elem]);
            setOpenHint(false);
        }
        setElemsListInput('');
    };

    return (
        <section className={`filter-section ${isBlackList ? "black-list-section" : "white-list-section"}`}>
          <h2 className="section-title">{sectionTitle}</h2>
          <p className="section-description">{sectionDescription}</p>
          
          <input
            type="text"
            className="filter-input"
            placeholder={`${inputPlaceholder}`}
            value={elemsListInput}
            onChange={(e) => setElemsListInput(e.target.value)}
            onKeyDown={handleElemsListAdd}
            onFocus={() => setOpenHint(true)}
            onBlur={() => setTimeout(() => setOpenHint(false), 150)}
          />
          {openHint && elemsListInput.trim() !== '' && filteredSuggestions.length > 0 && (
            <ul className="hint-list">
            {filteredSuggestions.map((elem) => (
                <li className="hint-option"
                key={elem.id}
                onMouseDown={() => handleSelect(elem)}
                >
                {elem.title}
                </li>
            ))}
            </ul>
        )}
          
          <div className="tags-container">
            {elemsList.map((tag) => (
              <span key={tag.id} className={`"tag ${isBlackList ? "tag-black" : "tag-white"}"`}>
                {tag.title}
                <button 
                  className="tag-remove" 
                  onClick={() => handleElemsListRemove(tag)}
                >
                  ×
                </button>
              </span>
            ))}
            {elemsList.length === 0 && (
              <span className="empty-message">{emptyMessage}</span>
            )}
          </div>
        </section>
    );
}