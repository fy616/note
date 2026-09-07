package 实验10.experiment_10;

import java.io.*;
import java.util.*;

/**
 * 歌曲管理系统主程序
 */
public class test {
    // 使用List存储歌曲列表
    private static List<Song> songList = new ArrayList<>();
    // 使用Map进行快速查找(按编号索引)
    private static Map<String, Song> songMap = new HashMap<>();
    // 数据文件路径
    private static final String DATA_FILE = "songs.txt";

    public static void main(String[] args) {
        // 加载已有数据
        loadData();
        
        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        
        while (running) {
            showMenu();
            System.out.print("请选择操作(1-5): ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // 消耗换行符
            
            switch (choice) {
                case 1:
                    searchSong(scanner);
                    break;
                case 2:
                    addSong(scanner);
                    break;
                case 3:
                    deleteSong(scanner);
                    break;
                case 4:
                    displayAllSongs();
                    break;
                case 5:
                    saveData();
                    running = false;
                    System.out.println("感谢使用,再见!");
                    break;
                default:
                    System.out.println("无效选择,请重新输入!");
            }
            System.out.println();
        }
        
        scanner.close();
    }

    /**
     * 显示菜单
     */
    private static void showMenu() {
        System.out.println("**************************************************");
        System.out.println("       ** 歌曲管理 **");
        System.out.println("**************************************************");
        System.out.println("   1------------------查找歌曲");
        System.out.println("   2------------------增加歌曲");
        System.out.println("   3------------------删除歌曲");
        System.out.println("   4------------------显示所有歌曲");
        System.out.println("   5------------------退出系统");
        System.out.println("**************************************************");
    }

    /**
     * 查找歌曲
     */
    private static void searchSong(Scanner scanner) {
        System.out.println("请选择查询方式:");
        System.out.println("1. 按歌名");
        System.out.println("2. 按语言");
        System.out.println("3. 按歌手");
        System.out.println("4. 按类别");
        System.out.print("请选择(1-4): ");
        int type = scanner.nextInt();
        scanner.nextLine();
        
        System.out.print("请输入查询关键字: ");
        String keyword = scanner.nextLine().trim();
        
        List<Song> results = new ArrayList<>();
        
        for (Song song : songList) {
            boolean match = false;
            switch (type) {
                case 1:
                    match = song.getName().contains(keyword);
                    break;
                case 2:
                    match = song.getLanguage().equals(keyword);
                    break;
                case 3:
                    match = song.getSinger().contains(keyword);
                    break;
                case 4:
                    match = song.getCategory().equals(keyword);
                    break;
            }
            if (match) {
                results.add(song);
            }
        }
        
        if (results.isEmpty()) {
            System.out.println("未找到相关歌曲!");
        } else {
            System.out.println("\n找到 " + results.size() + " 首歌曲:");
            for (Song song : results) {
                System.out.println(song);
            }
        }
    }

    /**
     * 添加歌曲
     */
    private static void addSong(Scanner scanner) {
        System.out.println("--- 添加新歌曲 ---");
        
        System.out.print("请输入歌曲编号: ");
        String id = scanner.nextLine().trim();
        
        // 检查编号是否已存在
        if (songMap.containsKey(id)) {
            System.out.println("该编号已存在!");
            return;
        }
        
        System.out.print("请输入歌名: ");
        String name = scanner.nextLine().trim();
        
        System.out.print("请输入语言(中文/英文): ");
        String language = scanner.nextLine().trim();
        
        System.out.print("请输入类别(流行/通俗): ");
        String category = scanner.nextLine().trim();
        
        System.out.print("请输入歌手: ");
        String singer = scanner.nextLine().trim();
        
        Song song = new Song(id, name, language, category, singer);
        songList.add(song);
        songMap.put(id, song);
        
        System.out.println("添加成功!");
    }

    /**
     * 删除歌曲
     */
    private static void deleteSong(Scanner scanner) {
        System.out.print("请输入要删除的歌曲编号: ");
        String id = scanner.nextLine().trim();
        
        Song song = songMap.get(id);
        if (song == null) {
            System.out.println("未找到该编号的歌曲!");
            return;
        }
        
        songList.remove(song);
        songMap.remove(id);
        
        System.out.println("删除成功!");
    }

    /**
     * 显示所有歌曲
     */
    private static void displayAllSongs() {
        if (songList.isEmpty()) {
            System.out.println("当前没有歌曲记录!");
            return;
        }
        
        System.out.println("\n=== 所有歌曲列表 ===");
        System.out.println("共 " + songList.size() + " 首歌曲:");
        for (int i = 0; i < songList.size(); i++) {
            System.out.println((i + 1) + ". " + songList.get(i));
        }
    }

    /**
     * 保存数据到文件(文本格式)
     */
    private static void saveData() {
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(DATA_FILE))) {
            for (Song song : songList) {
                // 格式: 编号|歌名|语言|类别|歌手
                String line = song.getId() + "|" + 
                             song.getName() + "|" + 
                             song.getLanguage() + "|" + 
                             song.getCategory() + "|" + 
                             song.getSinger();
                writer.write(line);
                writer.newLine();
            }
            System.out.println("数据已保存到文件!");
        } catch (IOException e) {
            System.err.println("保存数据失败: " + e.getMessage());
        }
    }

    /**
     * 从文件加载数据(文本格式)
     */
    private static void loadData() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            System.out.println("首次运行,创建新的数据库...");
            return;
        }
        
        try (BufferedReader reader = new BufferedReader(
                new FileReader(file))) {
            String line;
            int count = 0;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                
                // 解析格式: 编号|歌名|语言|类别|歌手
                String[] parts = line.split("\\|");
                if (parts.length == 5) {
                    Song song = new Song(
                        parts[0],  // id
                        parts[1],  // name
                        parts[2],  // language
                        parts[3],  // category
                        parts[4]   // singer
                    );
                    songList.add(song);
                    songMap.put(song.getId(), song);
                    count++;
                }
            }
            System.out.println("已加载 " + count + " 首歌曲!");
        } catch (IOException e) {
            System.err.println("加载数据失败: " + e.getMessage());
        }
    }
}
