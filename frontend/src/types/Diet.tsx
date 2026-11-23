import { Recipe } from './Recipe.tsx';

export interface Diet {
  id: number;
  description: string;
  recipes: Array<Array<Recipe>>
}