package imadz.team.efficiency.domain.service.git

object GitCommands {
  def common_prefix(dir: String) = s"git --git-dir=${dir}/.git --work-tree=${dir}"

  def log_full_history_pretty_fuller(dir: String): String = s"${common_prefix(dir)} log --full-history --pretty=fuller"

  def cat_file_p(dir: String)(commitId: String) = s"${common_prefix(dir)} cat-file -p ${commitId}"

  def diff_tree_full_index_r_M_C(dir: String)(commitId: String) = s"${common_prefix(dir)} diff-tree --full-index -r -M -C -B"
}
