declare module '*.vue' {
    import {DefineComponent} from "vue"
    const component: DefineComponent<{}, {}, any>
    export default component
}

export type Post = {
    frontMatter: {
        [key: string]: any;
    };
    regularPath: string;
}