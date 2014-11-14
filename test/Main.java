import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * User: Alpen Ditrix
 * Date: 01.05.2014
 * Time: 11:11
 */
public class Main {
    public static class BinaryHeap {
        private final List<Integer> heap;

        public BinaryHeap() {
            heap = new ArrayList<>();
        }

        private static int leftChild(int i) {
            return 2 * i + 1;
        }

        private static int rightChild(int i) {
            return 2 * (i + 1);
        }

        private static int parent(int i) {
            return (i - 1) / 2;
        }

        private int get(int index) {
            if (index < heap.size()) {
                return heap.get(index);
            } else {
                return Integer.MIN_VALUE;
            }
        }

        private boolean needToEmerge(int from) {
            return from > 0 && heap.get(from) > heap.get(parent(from));
        }

        private int emerge(int from) {
            int parentIndex = parent(from);
            swap(from, parentIndex);
            return parentIndex;
        }

        private void swap(int idx1, int idx2) {
            int tmp = heap.get(idx2);
            heap.set(idx2, heap.get(idx1));
            heap.set(idx1, tmp);
        }


        private int drownLeft(int from) {
            int index = leftChild(from);
            swap(index, from);
            return index;

        }

        private int drownRight(int from) {
            int index = rightChild(from);
            swap(index, from);
            return index;

        }

        public void insert(int key) {
            heap.add(key);
            int index = heap.size() - 1;
            while (needToEmerge(index)) {
                index = emerge(index);
            }
        }

        public int extractRoot() {
            int returnValue = heap.get(0);
            heap.set(0, get(heap.size() - 1));
            heap.remove(heap.size() - 1);
            int index = 0;
            while (index < heap.size()) {
                int drownPower = needDrown(index);
                if (drownPower == -1) {
                    index = drownLeft(index);
                } else if (drownPower == 1) {
                    index = drownRight(index);
                } else {
                    break;
                }
            }
            return returnValue;
        }

        private int needDrown(int index) {
            int current = get(index);
            int left = get(leftChild(index));
            int right = get(rightChild(index));
            if (left > right) {
                return left > current ? -1 : (right > current) ? 1 : 0;
            } else {
                return right > current ? 1 : (left > current) ? -1 : 0;
            }
        }


        public String toString() {
            StringBuilder sb = new StringBuilder();
            int index = 0;
            int level = 1;
            int nextLevelIndex = 0;

            while (index < heap.size()) {
                sb.append(heap.get(index));
                sb.append(' ');
                if (index++ == nextLevelIndex) {
                    sb.append('\n');
                    nextLevelIndex += 1 << level++;
                }
            }
            return sb.toString();
        }

        public int size() {
            return heap.size();
        }

    }

    public static void main(String[] args) {
        BinaryHeap heap = new BinaryHeap();
        Scanner sc = new Scanner(System.in);
        int N = sc.nextInt();
        for (int i = 0; i < N; i++) {
            if (sc.next().charAt(0) == 'I') {
                heap.insert(sc.nextInt());
            } else {
                System.out.println(heap.extractRoot());
            }
            System.out.println(heap);
            System.out.println();
        }
    }

}
