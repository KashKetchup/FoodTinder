import { Ingredient, Tag } from "./Tags";

export interface RecipeShort {
  id: number;
  title: string;
  description: string;
  image_url: string;
  cookingTime: number;
  rate: number;
  calories: number;
  proteins: number;
  fats: number;
  carbs: number;
  difficulty: number;
}

export interface Recipe extends RecipeShort {
  instruction: string;
  ingredients: Ingredient[];
  tags: Tag[];
}