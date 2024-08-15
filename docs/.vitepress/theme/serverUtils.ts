import {globby} from 'globby';
import fs from "fs-extra";
import matter from "gray-matter";
import {Post} from "../env";

export async function getPosts(): Promise<Array<Post>> {
    const paths = await getPostMDFilePaths();
    const posts: Array<Post> = await Promise.all(
        paths.map(async (item) => {
            const content = await fs.readFile(item, "utf-8");
            const {data} = matter(content);
            data.date = _convertDate(data.date);
            return {
                frontMatter: data,
                regularPath: `/${item.replace(".md", ".html")}`,
            };
        })
    );
    return posts.sort(_compareDate);
}

function _convertDate(date = new Date().toString()) {
    const json_date = new Date(date).toJSON();
    return json_date.split("T")[0];
}

function _compareDate(obj1: Post, obj2: Post) {
    return obj1.frontMatter.date < obj2.frontMatter.date ? 1 : -1;
}

async function getPostMDFilePaths() {
    let paths = await globby(["**.md"], {
        ignore: ["node_modules", "README.md"],
    });
    return paths.filter((item) => item.includes("posts/"));
}

export async function getPostLength() {
    // getPostMDFilePath return type is object not array
    return [...(await getPostMDFilePaths())].length;
}
