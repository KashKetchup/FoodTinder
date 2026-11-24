import { Recipe } from './Recipe.tsx';

export interface Diet {
  id: number;
  description: string;
  breakfast: Recipe;
  lunch: Recipe;
  dinner: Recipe; 
}