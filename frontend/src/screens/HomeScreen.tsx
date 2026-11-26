import { useNavigate } from 'react-router-dom';

export default function HomeScreen() {
    const navigate = useNavigate();

    return (
        <main>
            <h1>
                Приветственная страница
            </h1>
            <p>
                Здесь просто информация о приложении
            </p>
            <button className="btn-recipes" onClick={() => navigate('/recipes')}>
                К рецептам
            </button>
            <button className="btn-diets" onClick={() => navigate('/diets')}>
                Составить меню
            </button>
        </main>
    );
}