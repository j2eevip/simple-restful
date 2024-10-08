type Post = {
    frontMatter: {
        date?: string;
        title?: string;
        tags?: string[];
        description?: string;
    };
    regularPath: string;
};

type Head = {
    title: string,
    link: string,
    level: number,
    children: Head[]
}
export function initTags(post: Post[]) {
    const data: any = {};
    for (let i = 0; i < post.length; i++) {
        const element = post[i];
        const tags = element.frontMatter.tags;
        // tags是数组，需要tags按照数组语法的格式书写
        if (Array.isArray(tags)) {
            tags.forEach((item) => {
                if (!data[item]) {
                    data[item] = [];
                }
                data[item].push(element);
            });
        }
    }
    return data;
}

export function useYearSort(post: Post[]) {
    const data = [];
    let year = "0";
    let num = -1;
    for (let index = 0; index < post.length; index++) {
        const element = post[index];
        if (element.frontMatter.date) {
            const y = element.frontMatter.date.split("-")[0];
            if (y === year) {
                data[num].push(element);
            } else {
                num++;
                data[num] = [] as any;
                data[num].push(element);
                year = y;
            }
        }
    }
    return data;
}

export function getHeaders(range: string = ".VPDoc h2,h3,h4,h5,h6"): Head[] {
    const headers: NodeListOf<Element> = document.querySelectorAll(range);
    const result: Head[] = []
    headers.forEach((el: Element) => {
        if (!!el.id && el.hasChildNodes()) {
            const level: number = Number(el.tagName[1]);
            result.push({
                title: serializeHeader(el),
                link: "#" + el.id,
                level,
                children: []
            })
        }
    })
    return result;
}

function serializeHeader(h: Element): string {
    let ret = "";
    h.childNodes.forEach(node => {
        if (node.nodeType === 1) {
            if (!(node as Element).classList.contains("VPBadge") &&
                !(node as Element).classList.contains("header-anchor")) {
                ret += node.textContent;
            }
        } else if (node.nodeType === 3) {
            ret += node.textContent;
        }
    })
    return ret.trim();
}

export function resolveHeaders(headers: Head[], range?: any): any {
    if (range === false) {
        return [];
    }
    const minLevel = headers.reduce((min, header) => Math.min(header.level, min), 3);
    const levelsRange =
        (typeof range === "object" && !Array.isArray(range)
            ? range.level
            : range) || minLevel;

    console.log(levelsRange, "levelsRange");
    const [high, low]: [number, number] =
        typeof levelsRange === "number"
            ? [levelsRange, levelsRange]
            : levelsRange === "deep"
                ? [2, 6]
                : levelsRange;

    console.log(high, low, "loooww");
    headers = headers.filter((h) => h.level >= high && h.level <= low);

    const ret: any = [];
    outer: for (let i = 0; i < headers.length; i++) {
        const cur = headers[i];
        if (i === 0) {
            ret.push(cur);
        } else {
            for (let j = i - 1; j >= 0; j--) {
                const prev: Head = headers[j];
                if (prev.level < cur.level) {
                    (prev.children || (prev.children = [])).push(cur);
                    continue outer;
                }
            }
            ret.push(cur);
        }
    }

    return ret;
}
