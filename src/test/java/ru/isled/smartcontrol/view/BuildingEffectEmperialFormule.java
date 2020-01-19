package ru.isled.smartcontrol.view;

public class BuildingEffectEmperialFormule {
    public static void main(String[] args) {
        // 1 13 23 31
        // 10 20 28 34
        int sizeX = 8;
        int blockWidth = 2;
        int tailBefore = 2;
        int tailAfter = 3;

        // здесь выведена эмпирическая формула. ничего не понятно, но именно тут смотреть формулы

        int startLine = 0;
        int collectedBlocks = 0;
        int totalBlockWidth = tailAfter + tailBefore + blockWidth;
        for (int blockStage = 0; blockStage < Math.ceil((double) sizeX / blockWidth); blockStage++) {
            int endLine = startLine + totalBlockWidth + sizeX;
            int filledBlocks = startLine + (sizeX -  (collectedBlocks * blockWidth)) + tailBefore;
            System.out.println("startLine = " + startLine);
            System.out.println("filledBlocks = " + filledBlocks);
            System.out.println("endLine = " + endLine + "\n");
            startLine = endLine - tailAfter - (collectedBlocks++ * blockWidth);
        }
    }
}
